package com.example.demo.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.MarketCarCreateRequest;
import com.example.demo.dto.request.MarketCarUpdateRequest;
import com.example.demo.dto.response.MarketCarResponse;
import com.example.demo.models.entities.MarketCar;
import com.example.demo.models.entities.MarketCarItem;

@Component
public class MarketCarMapper {

    private final MarketCarItemMapper itemMapper;

    public MarketCarMapper(MarketCarItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public MarketCarResponse toResponse(MarketCar entity) {
        if (entity == null) {
            return null;
        }

        List<com.example.demo.dto.response.MarketCarItemResponse> items = entity.getItems() != null
            ? entity.getItems().stream().map(itemMapper::toResponse).collect(Collectors.toList())
            : null;

        Long clientId = entity.getClient() != null ? entity.getClient().getId() : null;

        return new MarketCarResponse(
            entity.getId(),
            clientId,
            items,
            entity.getTotalValue()
        );
    }

    public MarketCar toEntity(MarketCarCreateRequest request) {
        if (request == null) {
            return null;
        }

        MarketCar entity = new MarketCar();
        List<MarketCarItem> items = request.getItems() != null
            ? request.getItems().stream().map(itemMapper::toEntity).collect(Collectors.toList())
            : null;

        entity.setItems(items);
        entity.recalculateTotalValue();
        return entity;
    }

    public MarketCar toEntity(MarketCarUpdateRequest request, MarketCar entity) {
        if (request == null || entity == null) {
            return entity;
        }

        List<MarketCarItem> items = request.getItems() != null
            ? request.getItems().stream().map(itemMapper::toEntity).collect(Collectors.toList())
            : null;

        entity.setItems(items);
        entity.recalculateTotalValue();
        return entity;
    }
}
