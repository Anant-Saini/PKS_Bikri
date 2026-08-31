package com.perfectkode.bikri.auth.dto.response;

import java.time.Instant;

public record ApiResponse(
        boolean success,
        String message,
        Instant timestamp
) {
    public ApiResponse(boolean success, String message) {
        this(success, message, Instant.now());
    }
}
