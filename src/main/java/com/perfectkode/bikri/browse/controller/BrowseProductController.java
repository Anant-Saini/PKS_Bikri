package com.perfectkode.bikri.browse.controller;

import com.perfectkode.bikri.browse.dto.response.BrowseProductResponse;
import com.perfectkode.bikri.browse.service.BrowseProductService;
import com.perfectkode.bikri.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/browse/products")
@RequiredArgsConstructor
@Tag(name = "Public Product Browsing", description = "Public APIs for searching, filtering, and retrieving products")
public class BrowseProductController {

    private final BrowseProductService browseProductService;

    @Operation(summary = "Get all active products", description = "Fetches a paginated list of active products with customizable sorting")
    @GetMapping
    public ResponseEntity<PagedResponse<BrowseProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<BrowseProductResponse> response = browseProductService.getAllProducts(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search products by keyword", description = "Searches active products by name or description with pagination")
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<BrowseProductResponse>> searchProducts(
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<BrowseProductResponse> response = browseProductService.searchProducts(query, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by slug", description = "Retrieves details of a single active product by its unique SEO slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found or inactive")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BrowseProductResponse> getProductBySlug(@PathVariable("slug") String slug) {
        BrowseProductResponse response = browseProductService.getProductBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by ID", description = "Retrieves details of a single active product by its unique UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found or inactive")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrowseProductResponse> getProductById(@PathVariable("id") UUID id) {
        BrowseProductResponse response = browseProductService.getProductById(id);
        return ResponseEntity.ok(response);
    }
}