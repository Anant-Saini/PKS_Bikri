package com.perfectkode.bikri.common.mapper;

import com.perfectkode.bikri.admin.dto.response.InventoryResponse;
import com.perfectkode.bikri.admin.dto.response.ProductResponse;
import com.perfectkode.bikri.admin.model.Inventory;
import com.perfectkode.bikri.admin.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements Mapper<Product, ProductResponse> {

    @Override
    public ProductResponse toDto(Product entity) {
        if (entity == null) {
            return null;
        }

        InventoryResponse inventoryDto = null;
        if (entity.getInventory() != null) {
            Inventory inv = entity.getInventory();
            inventoryDto = new InventoryResponse(
                    inv.getId(),
                    inv.getQuantity(),
                    inv.isActive(),
                    inv.getVersion(),
                    inv.getUpdatedAt()
            );
        }

        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getPrice(),
                entity.isActive(),
                inventoryDto,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public Product toEntity(ProductResponse dto) {
        throw new UnsupportedOperationException("Direct DTO to Entity conversion is not supported for ProductResponse");
    }
}