package com.perfectkode.bikri.common.mapper;

import com.perfectkode.bikri.admin.dto.response.InventoryResponse;
import com.perfectkode.bikri.admin.dto.response.ProductResponse;
import com.perfectkode.bikri.admin.model.Inventory;
import com.perfectkode.bikri.admin.model.Product;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper implements Mapper<Inventory, InventoryResponse> {

    @Override
    public InventoryResponse toDto(Inventory entity) {
        if (entity == null) {
            return null;
        }

        return new InventoryResponse(
                entity.getId(),
                entity.getQuantity(),
                entity.isActive(),
                entity.getVersion(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public Inventory toEntity(InventoryResponse dto) {
        throw new UnsupportedOperationException("Direct DTO to Entity conversion is not supported for InventoryResponse");
    }
}
