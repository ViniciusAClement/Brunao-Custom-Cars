package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.MarketCarItemCreateRequest;
import com.example.demo.dto.request.MarketCarItemUpdateRequest;
import com.example.demo.dto.response.MarketCarItemResponse;
import com.example.demo.models.entities.MarketCarItem;

@Component
public class MarketCarItemMapper {

    public MarketCarItemResponse toResponse(MarketCarItem entity) {
        if (entity == null) {
            return null;
        }

        Long productId = entity.getProduct() != null ? entity.getProduct().getId() : null;

        return new MarketCarItemResponse(
            entity.getId(),
            productId,
            entity.getQuantity(),
            entity.getTotalValue()
        );
    }

    public MarketCarItem toEntity(MarketCarItemCreateRequest request) {
        if (request == null) {
            return null;
        }

        MarketCarItem entity = new MarketCarItem();
        entity.setQuantity(request.getQuantity());
        return entity;
    }

    public MarketCarItem toEntity(MarketCarItemUpdateRequest request, MarketCarItem entity) {
        if (request == null || entity == null) {
            return entity;
        }

        entity.setQuantity(request.getQuantity());
        return entity;
    }
}
