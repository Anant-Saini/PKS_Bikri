package com.perfectkode.bikri.auth.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        boolean isVerified,
        String role
) {}
