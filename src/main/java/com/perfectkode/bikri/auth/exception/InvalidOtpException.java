package com.perfectkode.bikri.auth.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class InvalidOtpException extends CustomApplicationException {

    public InvalidOtpException(String message) {
        super(
                message,
                HttpStatus.BAD_REQUEST,
                "INVALID_OTP"
        );
    }
}
