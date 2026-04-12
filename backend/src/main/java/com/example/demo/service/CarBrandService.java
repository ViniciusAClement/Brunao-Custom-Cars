package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CarBrandCreateRequest;
import com.example.demo.dto.request.CarBrandUpdateRequest;
import com.example.demo.dto.response.CarBrandResponse;
import com.example.demo.dto.mapper.CarBrandMapper;
import com.example.demo.models.entities.CarBrand;
import com.example.demo.repository.CarBrandRepository;

@Service
@Transactional
public class CarBrandService {

    private final CarBrandRepository repository;
    private final CarBrandMapper mapper;

    public CarBrandService(CarBrandRepository repository, CarBrandMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CarBrandResponse create(CarBrandCreateRequest request) {
        CarBrand entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public CarBrandResponse update(Long id, CarBrandUpdateRequest request) {
        CarBrand entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CarBrand not found: " + id));
        mapper.toEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public CarBrandResponse findById(Long id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("CarBrand not found: " + id));
    }

    public List<CarBrandResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
