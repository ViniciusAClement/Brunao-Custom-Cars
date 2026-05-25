package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.MarketCarCreateRequest;
import com.example.demo.dto.request.MarketCarUpdateRequest;
import com.example.demo.dto.response.MarketCarResponse;
import com.example.demo.service.MarketCarService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/market-cars")
public class MarketCarController {

    private final MarketCarService service;

    public MarketCarController(MarketCarService service) {
        this.service = service;
    }

    @PostMapping
    public MarketCarResponse create(@Valid @RequestBody MarketCarCreateRequest request, Authentication authentication) {
        return service.create(request, authentication);
    }

    @GetMapping
    public List<MarketCarResponse> findAll(Authentication authentication) {
        return service.findAll(authentication);
    }

    @GetMapping("/{id}")
    public MarketCarResponse findById(@PathVariable Long id, Authentication authentication) {
        return service.findById(id, authentication);
    }

    @GetMapping("/client/{clientId}")
    public MarketCarResponse findByClientId(@PathVariable Long clientId, Authentication authentication) {
        return service.findOrCreateByClientId(clientId, authentication);
    }

    @PutMapping("/{id}")
    public MarketCarResponse update(@PathVariable Long id, @Valid @RequestBody MarketCarUpdateRequest request,
            Authentication authentication) {
        return service.update(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, authentication);
    }
}
