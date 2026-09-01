package com.perfectkode.bikri.admin.controller;

import com.perfectkode.bikri.admin.dto.request.CreateProductRequest;
import com.perfectkode.bikri.admin.dto.request.UpdateInventoryRequest;
import com.perfectkode.bikri.admin.dto.request.UpdateProductRequest;
import com.perfectkode.bikri.admin.dto.response.InventoryResponse;
import com.perfectkode.bikri.admin.dto.response.ProductResponse;
import com.perfectkode.bikri.admin.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Product & Inventory Management", description = "APIs for creating, updating, and managing products and stock levels")
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product", description = "Creates a product with metadata and initializes inventory quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update product metadata", description = "Updates details like name, description, and price for an active product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found or inactive")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update inventory quantity", description = "Updates the stock quantity for a specific product using optimistic locking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "409", description = "Concurrency conflict - inventory updated by another transaction")
    })
    @PatchMapping("/{id}/inventory")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateInventoryRequest request) {
        InventoryResponse response = productService.updateInventory(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Soft-delete a product", description = "Deactivates a product and its associated inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product soft-deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Product not found or already inactive")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get List of Products", description = "Retrieves a list of all active products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> listProducts() {
        List<ProductResponse> responses = productService.listProducts();
        return ResponseEntity.ok(responses);
    }
}
