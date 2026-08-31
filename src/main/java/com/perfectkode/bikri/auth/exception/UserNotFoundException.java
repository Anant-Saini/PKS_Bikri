package com.perfectkode.bikri.auth.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomApplicationException {

    public UserNotFoundException(String message) {
        super(
                message,
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }
}
