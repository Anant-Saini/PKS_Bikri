package com.perfectkode.bikri.order.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends CustomApplicationException {

    public OrderNotFoundException(String message) {
        super(
                message,
                HttpStatus.NOT_FOUND,
                "ORDER_NOT_FOUND"
        );
    }
}
