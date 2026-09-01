package com.perfectkode.bikri.admin.exception;

import com.perfectkode.bikri.common.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;

public class InventoryNotFoundException extends CustomApplicationException {

    public InventoryNotFoundException(String message) {
        super(
                message,
                HttpStatus.NOT_FOUND,
                "INVENTORY_NOT_FOUND"
        );
    }
}
