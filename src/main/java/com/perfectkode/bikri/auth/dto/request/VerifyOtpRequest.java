package com.perfectkode.bikri.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "OTP is required")
        @Size(min = 4, max = 4, message = "OTP must be exactly 4 digits")
        String otp
) {}