package com.perfectkode.bikri.admin.dto.response;

import java.time.Instant;

public record InventoryResponse(
        Long id,
        Integer quantity,
        boolean active,
        Long version,
        Instant updatedAt
) {}