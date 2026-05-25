package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.dto.request.MarketCarItemCreateRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.MarketCarMapper;
import com.example.demo.dto.request.MarketCarCreateRequest;
import com.example.demo.dto.request.MarketCarUpdateRequest;
import com.example.demo.dto.response.MarketCarResponse;
import com.example.demo.models.entities.Client;
import com.example.demo.models.entities.MarketCar;
import com.example.demo.models.entities.MarketCarItem;
import com.example.demo.models.entities.Product;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.MarketCarRepository;
import com.example.demo.repository.ProductRepository;

@Service
@Transactional
public class MarketCarService {

    private final MarketCarRepository marketCarRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final MarketCarMapper mapper;
    private final MarketCarAccessService accessService;
    private final ProductService productService;

    public MarketCarService(
            MarketCarRepository marketCarRepository,
            ClientRepository clientRepository,
            ProductRepository productRepository,
            MarketCarMapper mapper,
            MarketCarAccessService accessService,
            ProductService productService) {
        this.marketCarRepository = marketCarRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.accessService = accessService;
        this.productService = productService;
    }

    public MarketCarResponse create(MarketCarCreateRequest request, Authentication authentication) {
        if (request == null) {
            throw new IllegalArgumentException("MarketCarCreateRequest cannot be null");
        }

        Long clientId = accessService.resolveClientId(request.getClientId(), authentication);
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));

        MarketCar marketCar = client.getMarketCar();
        if (marketCar == null) {
            marketCar = new MarketCar();
            marketCar.setClient(client);
            client.setMarketCar(marketCar);
        }

        setItemsFromRequest(request.getItems(), marketCar);
        marketCar.recalculateTotalValue();

        return mapper.toResponse(marketCarRepository.save(marketCar));
    }

    public MarketCarResponse update(Long id, MarketCarUpdateRequest request, Authentication authentication) {
        MarketCar marketCar = marketCarRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + id));
        accessService.ensureCanAccessMarketCar(marketCar, authentication);

        if (request == null) {
            throw new IllegalArgumentException("MarketCarUpdateRequest cannot be null");
        }

        setItemsFromRequest(request.getItems(), marketCar);
        marketCar.recalculateTotalValue();

        return mapper.toResponse(marketCarRepository.save(marketCar));
    }

    public MarketCarResponse findById(Long id, Authentication authentication) {
        MarketCar marketCar = marketCarRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + id));
        accessService.ensureCanAccessMarketCar(marketCar, authentication);
        return mapper.toResponse(marketCar);
    }

    public MarketCarResponse findByClientId(Long clientId, Authentication authentication) {
        accessService.ensureCanAccessClientId(clientId, authentication);
        return marketCarRepository.findByClientId(clientId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found for client: " + clientId));
    }

    public MarketCarResponse findOrCreateByClientId(Long clientId, Authentication authentication) {
        Long resolvedClientId = accessService.resolveClientId(clientId, authentication);
        return marketCarRepository.findByClientId(resolvedClientId)
            .map(mapper::toResponse)
            .orElseGet(() -> {
                Client client = clientRepository.findById(resolvedClientId)
                    .orElseThrow(() -> new IllegalArgumentException("Client not found: " + resolvedClientId));

                MarketCar marketCar = new MarketCar();
                marketCar.setClient(client);
                client.setMarketCar(marketCar);
                return mapper.toResponse(marketCarRepository.save(marketCar));
            });
    }

    public List<MarketCarResponse> findAll(Authentication authentication) {
        accessService.ensureCanListAllCarts(authentication);
        return marketCarRepository.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    public void delete(Long id, Authentication authentication) {
        MarketCar marketCar = marketCarRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + id));
        accessService.ensureCanAccessMarketCar(marketCar, authentication);

        releaseAllCartStock(marketCar);
        marketCarRepository.delete(marketCar);
    }

    private void setItemsFromRequest(List<MarketCarItemCreateRequest> itemRequests, MarketCar marketCar) {
        releaseAllCartStock(marketCar);
        marketCar.getItems().clear();

        if (itemRequests == null || itemRequests.isEmpty()) {
            return;
        }

        Map<Long, Integer> quantityByProductId = new LinkedHashMap<>();
        Map<Long, Product> productsById = new HashMap<>();

        for (MarketCarItemCreateRequest itemRequest : itemRequests) {
            if (itemRequest.getProductId() == null) {
                throw new IllegalArgumentException("ProductId cannot be null");
            }
            productService.validateQuantity(itemRequest.getQuantity());

            Product product = productsById.computeIfAbsent(itemRequest.getProductId(), productId ->
                productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId)));
            productService.ensureProductAvailableForSale(product);

            quantityByProductId.merge(itemRequest.getProductId(), itemRequest.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : quantityByProductId.entrySet()) {
            productService.reserveStock(productsById.get(entry.getKey()), entry.getValue());
        }

        for (MarketCarItemCreateRequest itemRequest : itemRequests) {
            Product product = productsById.get(itemRequest.getProductId());
            MarketCarItem item = new MarketCarItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setMarketCar(marketCar);
            marketCar.addItem(item);
        }
    }

    private void releaseAllCartStock(MarketCar marketCar) {
        if (marketCar == null || marketCar.getItems() == null) {
            return;
        }
        for (MarketCarItem item : new ArrayList<>(marketCar.getItems())) {
            if (item.getProduct() != null && item.getQuantity() != null) {
                productService.releaseStock(item.getProduct(), item.getQuantity());
            }
        }
    }
}
