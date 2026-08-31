package com.perfectkode.bikri.common.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String errorCode,
        String message,
        String path
) {
    public ErrorResponse(
            int status,
            String error,
            String errorCode,
            String message,
            String path
    ) {
        this(
                Instant.now(),
                status,
                error,
                errorCode,
                message,
                path
        );
    }
}
