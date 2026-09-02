package com.perfectkode.bikri.order.service;

import com.perfectkode.bikri.admin.exception.InventoryNotFoundException;
import com.perfectkode.bikri.admin.exception.ProductNotFoundException;
import com.perfectkode.bikri.admin.model.Inventory;
import com.perfectkode.bikri.admin.model.Product;
import com.perfectkode.bikri.admin.repository.ProductRepository;
import com.perfectkode.bikri.auth.exception.UserNotFoundException;
import com.perfectkode.bikri.auth.model.User;
import com.perfectkode.bikri.auth.repository.UserRepository;
import com.perfectkode.bikri.common.dto.PagedResponse;
import com.perfectkode.bikri.common.mapper.OrderMapper;
import com.perfectkode.bikri.order.dto.request.CreateOrderRequest;
import com.perfectkode.bikri.order.dto.request.OrderItemRequest;
import com.perfectkode.bikri.order.dto.response.OrderResponse;

import com.perfectkode.bikri.order.exception.BadRequestException;
import com.perfectkode.bikri.order.exception.InsufficientStockException;
import com.perfectkode.bikri.order.exception.OrderNotFoundException;
import com.perfectkode.bikri.order.model.Order;
import com.perfectkode.bikri.order.model.OrderItem;
import com.perfectkode.bikri.order.model.OrderStatus;
import com.perfectkode.bikri.order.repository.OrderRepository;
import com.perfectkode.bikri.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        // 1. Fetch ordering user
        User userProxy = userRepository.getReferenceById(userId);

        // 2. Consolidate duplicate products in request items
        Map<UUID, Integer> consolidatedItems = consolidateOrderItems(request.items());

        // 3. Create new Order entity
        Order order = new Order();
        order.setUser(userProxy);
        order.setStatus(OrderStatus.PLACED);

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        // 4. Process each item: validate stock, deduct inventory, build OrderItem
        for (Map.Entry<UUID, Integer> entry : consolidatedItems.entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQty = entry.getValue();

            Product product = productRepository.findById(productId)
                    .filter(Product::isActive)
                    .orElseThrow(() -> new ProductNotFoundException("Active product not found with ID: " + productId));

            Inventory inventory = product.getInventory();
            if (inventory == null || !inventory.isActive()) {
                throw new InventoryNotFoundException("Inventory not available for product: " + product.getName());
            }

            if (inventory.getQuantity() < requestedQty) {
                throw new InsufficientStockException(
                        "Insufficient stock for product '" + product.getName() +
                                "'. Requested: " + requestedQty + ", Available: " + inventory.getQuantity()
                );
            }

            // Deduct stock (Optimistic lock @Version will validate during flush)
            inventory.setQuantity(inventory.getQuantity() - requestedQty);

            // Compute subtotal with immutable unit price snapshot
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(requestedQty));
            calculatedTotal = calculatedTotal.add(itemSubtotal);

            // Build line item
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQty);
            orderItem.setUnitPrice(product.getPrice());

            // Bi-directional link
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(calculatedTotal);

        // 5. Persist aggregate root
        Order savedOrder = orderRepository.save(order);

        // 6. Return mapped response
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, UUID userId, boolean isAdmin) {
        // 1. Fetch order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        // 2. Ownership / Authorization verification
        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to cancel this order");
        }

        // 3. State transition validation
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Only orders in 'PLACED' status can be cancelled. Current status: " + order.getStatus());
        }

        // 4. Restore inventory stock levels
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null && product.getInventory() != null) {
                Inventory inventory = product.getInventory();
                inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            }
        }

        // 5. Update order state
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Only orders in 'PLACED' status can be marked as COMPLETED. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.COMPLETED);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getUserOrders(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        return mapToPagedResponse(orderPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId, UUID userId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to view this order");
        }

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        return mapToPagedResponse(orderPage);
    }

    // Private helper to DRY up pagination mapping
    private PagedResponse<OrderResponse> mapToPagedResponse(Page<Order> orderPage) {
        List<OrderResponse> content = orderPage.getContent()
                .stream()
                .map(orderMapper::toDto)
                .toList();

        return new PagedResponse<>(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }

    // Helper: Consolidates duplicate product requests into sum of quantities
    private Map<UUID, Integer> consolidateOrderItems(List<OrderItemRequest> items) {
        return items.stream().collect(Collectors.toMap(
                OrderItemRequest::productId,
                OrderItemRequest::quantity,
                Integer::sum
        ));
    }
}
