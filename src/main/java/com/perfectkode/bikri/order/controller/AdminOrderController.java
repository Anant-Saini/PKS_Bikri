package com.perfectkode.bikri.order.controller;

import com.perfectkode.bikri.common.dto.PagedResponse;
import com.perfectkode.bikri.order.dto.response.OrderResponse;
import com.perfectkode.bikri.order.model.OrderStatus;
import com.perfectkode.bikri.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Order Management", description = "Admin-only APIs for managing order fulfillment, state transitions, and system-wide audits")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all customer orders", description = "Fetches a paginated list of all system orders with optional filtering by status")
    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size) {


        PagedResponse<OrderResponse> response = orderService.getAllOrders(status, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mark order as COMPLETED", description = "Transitions an order status from PLACED to COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order marked as completed"),
            @ApiResponse(responseCode = "400", description = "Order cannot be completed from its current state"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable("id") UUID id) {
        OrderResponse response = orderService.completeOrder(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Admin cancel order", description = "Allows an administrator to cancel any customer order and restore inventory")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> adminCancelOrder(@PathVariable("id") UUID id) {
        OrderResponse response = orderService.cancelOrder(id, null, true);
        return ResponseEntity.ok(response);
    }
}
