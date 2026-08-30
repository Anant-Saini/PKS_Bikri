package com.perfectkode.bikri.auth.service;

import com.perfectkode.bikri.auth.dto.request.LoginRequest;
import com.perfectkode.bikri.auth.dto.request.RegisterRequest;
import com.perfectkode.bikri.auth.dto.response.AuthResponse;
import com.perfectkode.bikri.auth.exception.UserAlreadyExistsException;
import com.perfectkode.bikri.auth.model.Role;
import com.perfectkode.bikri.auth.model.User;
import com.perfectkode.bikri.auth.repository.RoleRepository;
import com.perfectkode.bikri.auth.repository.UserRepository;
import com.perfectkode.bikri.security.jwt.JwtTokenProvider;
import com.perfectkode.bikri.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
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

        // 4. Authenticate the newly registered user to generate token directly
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.email(), registerRequest.password())
        );

        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().getRoleName()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        // 1. Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        // 2. Generate JWT Token
        String token = jwtTokenProvider.generateToken(authentication);

        // 3. Extract User Details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }
}