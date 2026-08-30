package com.perfectkode.bikri.common.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    // Convenience canonical constructor setting timestamp automatically if omitted
    public ErrorResponse(int status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path);
    }
}
