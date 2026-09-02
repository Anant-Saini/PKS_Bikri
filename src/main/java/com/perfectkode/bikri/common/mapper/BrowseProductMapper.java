package com.perfectkode.bikri.common.mapper;

import com.perfectkode.bikri.admin.model.Inventory;
import com.perfectkode.bikri.admin.model.Product;
import com.perfectkode.bikri.browse.dto.response.BrowseProductResponse;

import org.springframework.stereotype.Component;

@Component
public class BrowseProductMapper implements Mapper<Product, BrowseProductResponse> {

    @Override
    public BrowseProductResponse toDto(Product entity) {
        if (entity == null) {
            return null;
        }

        int stock = 0;
        Inventory inventory = entity.getInventory();
        if (inventory != null && inventory.isActive()) {
            stock = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        }

        return new BrowseProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getPrice(),
                stock,
                stock > 0
        );
    }

    @Override
    public Product toEntity(BrowseProductResponse dto) {
        throw new UnsupportedOperationException("Direct DTO to Entity conversion is not supported for BrowseProductResponse");
    }
}
