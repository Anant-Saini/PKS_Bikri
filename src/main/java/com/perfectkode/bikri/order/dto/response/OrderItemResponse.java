package com.perfectkode.bikri.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        Long id,
        UUID productId,
        String productName,
        String productSlug,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subTotal
) {}
