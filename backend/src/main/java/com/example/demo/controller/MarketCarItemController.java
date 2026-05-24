package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.MarketCarItemCreateRequest;
import com.example.demo.dto.request.MarketCarItemUpdateRequest;
import com.example.demo.dto.response.MarketCarItemResponse;
import com.example.demo.service.MarketCarItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/market-car-items")
public class MarketCarItemController {

    private final MarketCarItemService service;

    public MarketCarItemController(MarketCarItemService service) {
        this.service = service;
    }

    @PostMapping("/market-car/{marketCarId}")
    public MarketCarItemResponse addItem(@PathVariable Long marketCarId, @Valid @RequestBody MarketCarItemCreateRequest request) {
        return service.addItem(marketCarId, request);
    }

    @GetMapping("/{id}")
    public MarketCarItemResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/market-car/{marketCarId}")
    public List<MarketCarItemResponse> findAllByMarketCar(@PathVariable Long marketCarId) {
        return service.findAllByMarketCar(marketCarId);
    }

    @PutMapping("/{id}")
    public MarketCarItemResponse update(@PathVariable Long id, @Valid @RequestBody MarketCarItemUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
