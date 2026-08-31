package com.perfectkode.bikri.auth.service.auth;

import com.perfectkode.bikri.auth.dto.request.LoginRequest;
import com.perfectkode.bikri.auth.dto.request.RegisterRequest;
import com.perfectkode.bikri.auth.dto.request.VerifyOtpRequest;
import com.perfectkode.bikri.auth.dto.response.ApiResponse;
import com.perfectkode.bikri.auth.dto.response.AuthResponse;
import com.perfectkode.bikri.auth.exception.AccountNotVerifiedException;
import com.perfectkode.bikri.auth.exception.InvalidOtpException;
import com.perfectkode.bikri.auth.exception.UserAlreadyExistsException;
import com.perfectkode.bikri.auth.exception.UserNotFoundException;
import com.perfectkode.bikri.auth.model.Role;
import com.perfectkode.bikri.auth.model.User;
import com.perfectkode.bikri.auth.repository.RoleRepository;
import com.perfectkode.bikri.auth.repository.UserRepository;
import com.perfectkode.bikri.auth.service.otp.OtpService;
import com.perfectkode.bikri.security.jwt.JwtTokenProvider;
import com.perfectkode.bikri.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;

    @Override
    @Transactional
    public ApiResponse register(RegisterRequest registerRequest) {
        // 1. Check if email is already registered
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new UserAlreadyExistsException("User with email " + registerRequest.email() + " already exists");
        }

        // 2. Fetch default ROLE_USER
        Role userRole = roleRepository.findByRoleCode(1001)
                .orElseThrow(() -> new RuntimeException("Default ROLE_USER not found in database"));

        // 3. Create and save new User entity
        User user = new User(
                null,
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()),
                false, // isVerified
                true, // isEnabled
                null, // createdAt will be set automatically
                userRole
        );
        User savedUser = userRepository.save(user);

        //4. Trigger OTP generation and mock sending
        otpService.sendOtp(user.getEmail());

        return new ApiResponse(true,
                "Registration successful. Please verify your email with the OTP sent.");
    }

    @Override
    @Transactional
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.email()));

        if (user.isVerified()) {
            return new ApiResponse(true, "Email is already verified. Please login.");
        }

        boolean isValid = otpService.verifyOtp(request.email(), request.otp());
        if (!isValid) {
            throw new InvalidOtpException("Invalid or expired OTP code.");
        }

        user.setVerified(true);
        userRepository.save(user);

        return new ApiResponse(true, "Email verified successfully. You can now login.");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

        // Check verification status before authenticating
        if (!user.isVerified()) {
            throw new AccountNotVerifiedException("Account is not verified. Please verify your email first.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().getRoleName());
    }
}