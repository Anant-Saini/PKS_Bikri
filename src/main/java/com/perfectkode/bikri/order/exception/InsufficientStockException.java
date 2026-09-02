package com.perfectkode.bikri.order.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends CustomApplicationException {

    public InsufficientStockException(String message) {
        super(
                message,
                HttpStatus.CONFLICT,
                "NOT_ENOUGH_STOCK"
        );
    }
}
