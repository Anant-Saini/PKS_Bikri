package com.perfectkode.bikri.admin.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends CustomApplicationException {

    public ProductNotFoundException(String message) {
        super(
                message,
                HttpStatus.NOT_FOUND,
                "PRODUCT_NOT_FOUND"
        );
    }
}
