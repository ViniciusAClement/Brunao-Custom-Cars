package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
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
    private final MarketCarAccessService accessService;
    private final ProductService productService;

    public MarketCarItemService(
            MarketCarItemRepository itemRepository,
            MarketCarRepository marketCarRepository,
            ProductRepository productRepository,
            MarketCarItemMapper mapper,
            MarketCarAccessService accessService,
            ProductService productService) {
        this.itemRepository = itemRepository;
        this.marketCarRepository = marketCarRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.accessService = accessService;
        this.productService = productService;
    }

    public MarketCarItemResponse addItem(Long marketCarId, MarketCarItemCreateRequest request, Authentication authentication) {
        MarketCar marketCar = marketCarRepository.findById(marketCarId)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + marketCarId));
        accessService.ensureCanAccessMarketCar(marketCar, authentication);

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.getProductId()));
        productService.ensureProductAvailableForSale(product);

        productService.validateQuantity(request.getQuantity());

        MarketCarItem item = marketCar.getItems().stream()
            .filter(existing -> existing.getProduct() != null && existing.getProduct().getId().equals(product.getId()))
            .findFirst()
            .orElse(null);

        productService.reserveStock(product, request.getQuantity());

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

    public MarketCarItemResponse update(Long id, MarketCarItemUpdateRequest request, Authentication authentication) {
        MarketCarItem item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCarItem not found: " + id));
        accessService.ensureCanAccessMarketCarItem(item, authentication);

        productService.validateQuantity(request.getQuantity());

        Product product = item.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product not found for cart item: " + id);
        }
        productService.ensureProductAvailableForSale(product);

        int previousQuantity = item.getQuantity() != null ? item.getQuantity() : 0;
        productService.adjustReservedStock(product, previousQuantity, request.getQuantity());

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

    public MarketCarItemResponse findById(Long id, Authentication authentication) {
        MarketCarItem item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCarItem not found: " + id));
        accessService.ensureCanAccessMarketCarItem(item, authentication);
        return mapper.toResponse(item);
    }

    public List<MarketCarItemResponse> findAllByMarketCar(Long marketCarId, Authentication authentication) {
        MarketCar marketCar = marketCarRepository.findById(marketCarId)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + marketCarId));
        accessService.ensureCanAccessMarketCar(marketCar, authentication);

        return marketCar.getItems().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    public void delete(Long id, Authentication authentication) {
        MarketCarItem item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCarItem not found: " + id));
        accessService.ensureCanAccessMarketCarItem(item, authentication);

        if (item.getProduct() != null && item.getQuantity() != null) {
            productService.releaseStock(item.getProduct(), item.getQuantity());
        }

        MarketCar marketCar = item.getMarketCar();
        if (marketCar != null) {
            marketCar.removeItem(item);
            marketCarRepository.save(marketCar);
        } else {
            itemRepository.delete(item);
        }
    }

}
