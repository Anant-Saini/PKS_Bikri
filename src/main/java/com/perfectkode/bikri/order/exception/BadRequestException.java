package com.perfectkode.bikri.order.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomApplicationException {

    public BadRequestException(String message) {
        super(
                message,
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST"
        );
    }
}
