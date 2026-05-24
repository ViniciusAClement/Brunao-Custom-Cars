package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.MarketCarItemMapper;
import com.example.demo.dto.request.MarketCarItemCreateRequest;
import com.example.demo.dto.request.MarketCarItemUpdateRequest;
import com.example.demo.dto.response.MarketCarItemResponse;
import com.example.demo.models.entities.MarketCar;
import com.example.demo.models.entities.MarketCarItem;
import com.example.demo.models.entities.Product;
import com.example.demo.repository.MarketCarItemRepository;
import com.example.demo.repository.MarketCarRepository;
import com.example.demo.repository.ProductRepository;

@Service
@Transactional
public class MarketCarItemService {

    private final MarketCarItemRepository itemRepository;
    private final MarketCarRepository marketCarRepository;
    private final ProductRepository productRepository;
    private final MarketCarItemMapper mapper;

    public MarketCarItemService(
            MarketCarItemRepository itemRepository,
            MarketCarRepository marketCarRepository,
            ProductRepository productRepository,
            MarketCarItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.marketCarRepository = marketCarRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public MarketCarItemResponse addItem(Long marketCarId, MarketCarItemCreateRequest request) {
        MarketCar marketCar = marketCarRepository.findById(marketCarId)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + marketCarId));

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.getProductId()));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        MarketCarItem item = marketCar.getItems().stream()
            .filter(existing -> existing.getProduct() != null && existing.getProduct().getId().equals(product.getId()))
            .findFirst()
            .orElse(null);

        if (item == null) {
            item = new MarketCarItem();
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setMarketCar(marketCar);
            marketCar.addItem(item);
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.recalculateTotalValue();
            marketCar.recalculateTotalValue();
        }

        marketCarRepository.save(marketCar);
        return mapper.toResponse(item);
    }

    public MarketCarItemResponse update(Long id, MarketCarItemUpdateRequest request) {
        MarketCarItem item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCarItem not found: " + id));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        item.setQuantity(request.getQuantity());
        item.recalculateTotalValue();

        MarketCar marketCar = item.getMarketCar();
        if (marketCar != null) {
            marketCar.recalculateTotalValue();
            marketCarRepository.save(marketCar);
        } else {
            itemRepository.save(item);
        }

        return mapper.toResponse(item);
    }

    public MarketCarItemResponse findById(Long id) {
        return itemRepository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("MarketCarItem not found: " + id));
    }

    public List<MarketCarItemResponse> findAllByMarketCar(Long marketCarId) {
        MarketCar marketCar = marketCarRepository.findById(marketCarId)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + marketCarId));

        return marketCar.getItems().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        MarketCarItem item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCarItem not found: " + id));

        MarketCar marketCar = item.getMarketCar();
        if (marketCar != null) {
            marketCar.removeItem(item);
            marketCarRepository.save(marketCar);
        } else {
            itemRepository.delete(item);
        }
    }
}
