package com.perfectkode.bikri.common.mapper;

import com.perfectkode.bikri.order.dto.response.OrderItemResponse;
import com.perfectkode.bikri.order.dto.response.OrderResponse;
import com.perfectkode.bikri.order.model.Order;
import com.perfectkode.bikri.order.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class OrderMapper implements Mapper<Order, OrderResponse> {

    @Override
    public OrderResponse toDto(Order entity) {
        if (entity == null) {
            return null;
        }

        List<OrderItemResponse> itemDtos = entity.getItems() == null ? Collections.emptyList() :
                entity.getItems().stream()
                        .map(this::toItemDto)
                        .toList();

        return new OrderResponse(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getStatus(),
                entity.getTotalAmount(),
                itemDtos,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private OrderItemResponse toItemDto(OrderItem item) {
        BigDecimal subTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getSlug(),
                item.getQuantity(),
                item.getUnitPrice(),
                subTotal
        );
    }

    @Override
    public Order toEntity(OrderResponse dto) {
        throw new UnsupportedOperationException("Direct DTO to Entity conversion is not supported for OrderResponse");
    }
}
