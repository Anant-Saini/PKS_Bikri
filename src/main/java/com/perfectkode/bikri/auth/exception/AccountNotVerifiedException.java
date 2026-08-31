package com.perfectkode.bikri.auth.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class AccountNotVerifiedException extends CustomApplicationException {

    public AccountNotVerifiedException(String message) {
        super(
                message,
                HttpStatus.FORBIDDEN,
                "ACCOUNT_NOT_VERIFIED"
        );
    }
}