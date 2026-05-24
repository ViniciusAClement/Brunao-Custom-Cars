package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

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

    public MarketCarService(
            MarketCarRepository marketCarRepository,
            ClientRepository clientRepository,
            ProductRepository productRepository,
            MarketCarMapper mapper) {
        this.marketCarRepository = marketCarRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public MarketCarResponse create(MarketCarCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("MarketCarCreateRequest cannot be null");
        }

        Client client = clientRepository.findById(request.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + request.getClientId()));

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

    public MarketCarResponse update(Long id, MarketCarUpdateRequest request) {
        MarketCar marketCar = marketCarRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + id));

        if (request == null) {
            throw new IllegalArgumentException("MarketCarUpdateRequest cannot be null");
        }

        setItemsFromRequest(request.getItems(), marketCar);
        marketCar.recalculateTotalValue();

        return mapper.toResponse(marketCarRepository.save(marketCar));
    }

    public MarketCarResponse findById(Long id) {
        return marketCarRepository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + id));
    }

    public MarketCarResponse findByClientId(Long clientId) {
        return marketCarRepository.findByClientId(clientId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found for client: " + clientId));
    }

    public List<MarketCarResponse> findAll() {
        return marketCarRepository.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        MarketCar marketCar = marketCarRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("MarketCar not found: " + id));

        marketCarRepository.delete(marketCar);
    }

    private void setItemsFromRequest(List<com.example.demo.dto.request.MarketCarItemCreateRequest> itemRequests, MarketCar marketCar) {
        marketCar.getItems().clear();

        if (itemRequests == null || itemRequests.isEmpty()) {
            return;
        }

        for (com.example.demo.dto.request.MarketCarItemCreateRequest itemRequest : itemRequests) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));

            MarketCarItem item = new MarketCarItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setMarketCar(marketCar);
            marketCar.addItem(item);
        }
    }
}
