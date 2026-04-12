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

import com.example.demo.dto.request.CarBrandCreateRequest;
import com.example.demo.dto.request.CarBrandUpdateRequest;
import com.example.demo.dto.response.CarBrandResponse;
import com.example.demo.service.CarBrandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/car-brands")
public class CarBrandController {

    private final CarBrandService service;

    public CarBrandController(CarBrandService service) {
        this.service = service;
    }

    @PostMapping
    public CarBrandResponse create(@Valid @RequestBody CarBrandCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<CarBrandResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CarBrandResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public CarBrandResponse update(@PathVariable Long id, @Valid @RequestBody CarBrandUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
