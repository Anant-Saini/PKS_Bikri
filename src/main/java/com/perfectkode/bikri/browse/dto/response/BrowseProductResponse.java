package com.perfectkode.bikri.browse.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BrowseProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        boolean inStock
) {}
