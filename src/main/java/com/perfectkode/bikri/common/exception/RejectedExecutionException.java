package com.perfectkode.bikri.common.exception;

import org.springframework.http.HttpStatus;

public class RejectedExecutionException extends CustomApplicationException {

    public RejectedExecutionException(String message) {
        super(
                "Server is currently handling too many requests. Please try again in a few moments.",
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED"
        );
    }
}
