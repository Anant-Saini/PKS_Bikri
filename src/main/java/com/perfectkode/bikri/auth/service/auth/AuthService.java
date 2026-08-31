package com.perfectkode.bikri.auth.service.auth;

import com.perfectkode.bikri.auth.dto.request.LoginRequest;
import com.perfectkode.bikri.auth.dto.request.RegisterRequest;
import com.perfectkode.bikri.auth.dto.request.VerifyOtpRequest;
import com.perfectkode.bikri.auth.dto.response.ApiResponse;
import com.perfectkode.bikri.auth.dto.response.AuthResponse;

public interface AuthService {
    ApiResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    ApiResponse verifyOtp(VerifyOtpRequest request);
}