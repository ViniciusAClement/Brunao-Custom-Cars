package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.mapper.MarketCarItemMapper;
import com.example.demo.dto.request.MarketCarItemCreateRequest;
import com.example.demo.dto.response.MarketCarItemResponse;
import com.example.demo.models.entities.MarketCar;
import com.example.demo.models.entities.MarketCarItem;
import com.example.demo.models.entities.Product;
import com.example.demo.repository.MarketCarItemRepository;
import com.example.demo.repository.MarketCarRepository;
import com.example.demo.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class MarketCarItemServiceTest {

    @Mock
    private MarketCarItemRepository itemRepository;

    @Mock
    private MarketCarRepository marketCarRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MarketCarItemMapper mapper;

    @Mock
    private MarketCarAccessService accessService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private MarketCarItemService service;

    @BeforeEach
    void allowAccess() {
        doNothing().when(accessService).ensureCanAccessMarketCar(any(), any());
        doNothing().when(productService).ensureProductAvailableForSale(any());
        doNothing().when(productService).reserveStock(any(), any(Integer.class));
    }

    @Test
    void addItem_shouldCreateNewItemAndRecalculateTotalValue() {
        MarketCar marketCar = new MarketCar();
        marketCar.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(2L);
        product.setPrice(50.0);
        product.setStock(10);

        when(marketCarRepository.findById(1L)).thenReturn(Optional.of(marketCar));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(mapper.toResponse(any(MarketCarItem.class))).thenReturn(new MarketCarItemResponse(1L, 2L, 3, 150.0));

        MarketCarItemResponse response = service.addItem(1L, new MarketCarItemCreateRequest(2L, 3), null);

        verify(productService).reserveStock(product, 3);
        verify(marketCarRepository).save(marketCar);
        assertEquals(1, marketCar.getItems().size());
        MarketCarItem item = marketCar.getItems().get(0);
        assertEquals(product, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(150.0, item.getTotalValue());
        assertEquals(150.0, marketCar.getTotalValue());
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getProductId());
    }

    @Test
    void addItem_shouldRejectWhenQuantityExceedsStock() {
        MarketCar marketCar = new MarketCar();
        marketCar.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(2L);
        product.setPrice(50.0);
        product.setStock(2);

        when(marketCarRepository.findById(1L)).thenReturn(Optional.of(marketCar));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        doThrow(new IllegalArgumentException("Quantidade solicitada excede o estoque disponível (2)"))
            .when(productService).reserveStock(eq(product), eq(5));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.addItem(1L, new MarketCarItemCreateRequest(2L, 5), null)
        );

        assertEquals("Quantidade solicitada excede o estoque disponível (2)", ex.getMessage());
    }
}
