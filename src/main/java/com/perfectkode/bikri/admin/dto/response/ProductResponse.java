package com.perfectkode.bikri.admin.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        boolean active,
        InventoryResponse inventory,
        Instant createdAt,
        Instant updatedAt
) {}
