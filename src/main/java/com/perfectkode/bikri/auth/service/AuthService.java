package com.perfectkode.bikri.auth.service;

import com.perfectkode.bikri.auth.dto.request.LoginRequest;
import com.perfectkode.bikri.auth.dto.request.RegisterRequest;
import com.perfectkode.bikri.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}