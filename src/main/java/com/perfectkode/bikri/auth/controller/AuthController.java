package com.perfectkode.bikri.auth.controller;

import com.perfectkode.bikri.auth.dto.request.LoginRequest;
import com.perfectkode.bikri.auth.dto.request.RegisterRequest;
import com.perfectkode.bikri.auth.dto.request.ResendOtpRequest;
import com.perfectkode.bikri.auth.dto.request.VerifyOtpRequest;
import com.perfectkode.bikri.auth.dto.response.ApiResponse;
import com.perfectkode.bikri.auth.dto.response.AuthResponse;
import com.perfectkode.bikri.auth.service.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Management", description = "API for managing authentication")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        ApiResponse response = authService.verifyOtp(verifyOtpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@Valid @RequestBody ResendOtpRequest resendOtpRequest) {
        ApiResponse response = authService.resendOtp(resendOtpRequest);
        return ResponseEntity.ok(response);
    }
}
