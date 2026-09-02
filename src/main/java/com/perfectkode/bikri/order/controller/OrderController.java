package com.perfectkode.bikri.order.controller;

import com.perfectkode.bikri.common.dto.PagedResponse;
import com.perfectkode.bikri.order.dto.request.CreateOrderRequest;
import com.perfectkode.bikri.order.dto.response.OrderResponse;
import com.perfectkode.bikri.order.service.OrderService;
import com.perfectkode.bikri.security.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Tag(name = "Customer Order Management", description = "APIs for placing, viewing, and cancelling customer orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place a new order", description = "Creates a new order for the authenticated user and reserves product stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "One or more products not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.createOrder(userDetails.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get current user's order history", description = "Fetches a paginated list of orders placed by the authenticated user")
    @GetMapping("/my-orders")
    public ResponseEntity<PagedResponse<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size) {

        PagedResponse<OrderResponse> response = orderService.getUserOrders(userDetails.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get order details by ID", description = "Retrieves an order by its ID if it belongs to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "400", description = "Access denied for this order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") UUID id) {

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        OrderResponse response = orderService.getOrderById(id, userDetails.getId(), isAdmin);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel an order", description = "Cancels a PLACED order and restores inventory stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled (already completed or cancelled)"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") UUID id) {

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        OrderResponse response = orderService.cancelOrder(id, userDetails.getId(), isAdmin);
        return ResponseEntity.ok(response);
    }
}
