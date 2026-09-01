package com.perfectkode.bikri.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateInventoryRequest(
        @NotNull(message = "Quantity is required")
        @PositiveOrZero(message = "Inventory quantity cannot be negative")
        Integer quantity
) {}
