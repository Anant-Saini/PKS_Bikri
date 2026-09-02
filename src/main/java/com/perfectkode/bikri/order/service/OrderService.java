package com.perfectkode.bikri.order.service;

import com.perfectkode.bikri.common.dto.PagedResponse;
import com.perfectkode.bikri.order.dto.request.CreateOrderRequest;
import com.perfectkode.bikri.order.dto.response.OrderResponse;
import com.perfectkode.bikri.order.model.OrderStatus;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID userId, CreateOrderRequest request);

    OrderResponse cancelOrder(UUID orderId, UUID userId, boolean isAdmin);

    OrderResponse completeOrder(UUID orderId);

    PagedResponse<OrderResponse> getUserOrders(UUID userId, int page, int size);

    OrderResponse getOrderById(UUID orderId, UUID userId, boolean isAdmin);

    PagedResponse<OrderResponse> getAllOrders(OrderStatus status, int page, int size);
}