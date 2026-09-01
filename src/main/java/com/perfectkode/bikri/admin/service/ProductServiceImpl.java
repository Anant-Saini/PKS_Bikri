package com.perfectkode.bikri.admin.service;

import com.perfectkode.bikri.admin.dto.request.CreateProductRequest;
import com.perfectkode.bikri.admin.dto.request.UpdateInventoryRequest;
import com.perfectkode.bikri.admin.dto.request.UpdateProductRequest;
import com.perfectkode.bikri.admin.dto.response.InventoryResponse;
import com.perfectkode.bikri.admin.dto.response.ProductResponse;
import com.perfectkode.bikri.admin.exception.ProductNotFoundException;
import com.perfectkode.bikri.admin.model.Inventory;
import com.perfectkode.bikri.admin.model.Product;
import com.perfectkode.bikri.admin.repository.InventoryRepository;
import com.perfectkode.bikri.admin.repository.ProductRepository;
import com.perfectkode.bikri.common.mapper.InventoryMapper;
import com.perfectkode.bikri.common.mapper.ProductMapper;
import com.perfectkode.bikri.common.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // 1. Generate unique slug using our collision-aware utility
        String slug = SlugUtils.generateUniqueSlug(request.name(), productRepository::existsBySlug);

        // 2. Build Product entity
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSlug(slug);
        product.setActive(true);

        // 3. Initialize associated Inventory entity
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setActive(true);
        inventory.setQuantity(Objects.requireNonNullElse(request.initialQuantity(), 0));

        // 4. Link child to parent (Bi-directional link)
        product.setInventory(inventory);

        // 5. Save aggregate root (Cascades to Inventory automatically)
        Product savedProduct = productRepository.save(product);

        // 6. Map to Response DTO
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        // 1. Fetch existing product
        Product existingProduct = fetchActiveProduct(productId);
        // 2. Regenerate slug only if the product name changed
        if( !existingProduct.getName().equalsIgnoreCase(request.name()) ) {
            String newSlug = SlugUtils.generateUniqueSlug(request.name(), productRepository::existsBySlug);
            existingProduct.setName(request.name());
            existingProduct.setSlug(newSlug);
        }
        // 3. Update remaining metadata fields
        existingProduct.setDescription(request.description());
        existingProduct.setPrice(request.price());
        // 4. Save and return mapped DTO (Dirty checking updates DB on transaction commit)
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(UUID productId, UpdateInventoryRequest request) {
        // Fetch product to ensure it exists and is active
        Product existingProduct = fetchActiveProduct(productId);
        Inventory inventory = existingProduct.getInventory();
        if (inventory == null || !inventory.isActive()) {
            inventory = new Inventory();
            inventory.setProduct(existingProduct);
            inventory.setActive(true);
        }
        // Update quantity (Optimistic locking via @Version manages race conditions during save)
        inventory.setQuantity(request.quantity());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {
        //Fetch Active Product and mark it and its inventory as inactive (Soft delete)
        Product existingProduct = fetchActiveProduct(productId);
        existingProduct.setActive(false);
        // Deactivate Inventory child entity as well
        if (existingProduct.getInventory() != null) {
            existingProduct.getInventory().setActive(false);
        }
        productRepository.save(existingProduct);
    }

    @Override
    public List<ProductResponse> listProducts() {
        return productRepository.findByActive(true)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    private Product fetchActiveProduct(UUID productId) {
        return productRepository.findByIdAndActive(productId, true)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }
}
