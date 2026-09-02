package com.perfectkode.bikri.admin.service;

import com.perfectkode.bikri.admin.dto.request.CreateProductRequest;
import com.perfectkode.bikri.admin.dto.request.UpdateInventoryRequest;
import com.perfectkode.bikri.admin.dto.request.UpdateProductRequest;
import com.perfectkode.bikri.admin.dto.response.InventoryResponse;
import com.perfectkode.bikri.admin.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(UUID productId, UpdateProductRequest request);

    InventoryResponse updateInventory(UUID productId, UpdateInventoryRequest request);

    void deleteProduct(UUID productId);

    List<ProductResponse> listProducts();

    ProductResponse getProductById(UUID id);
}
