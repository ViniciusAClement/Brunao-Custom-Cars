package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CarCreateRequest;
import com.example.demo.dto.request.CarUpdateRequest;
import com.example.demo.dto.response.CarResponse;
import com.example.demo.dto.mapper.CarMapper;
import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.CarBrand;
import com.example.demo.repository.CarBrandRepository;
import com.example.demo.repository.CarRepository;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandRepository brandRepository;
    private final CarMapper mapper;

    public CarService(CarRepository carRepository, CarBrandRepository brandRepository, CarMapper mapper) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.mapper = mapper;
    }

    public CarResponse create(CarCreateRequest request) {
        Car entity = mapper.toEntity(request);
        CarBrand brand = brandRepository.findById(request.getCarBrandId())
            .orElseThrow(() -> new IllegalArgumentException("CarBrand not found: " + request.getCarBrandId()));
        entity.setCarBrand(brand);
        return mapper.toResponse(carRepository.save(entity));
    }

    public CarResponse update(Long id, CarUpdateRequest request) {
        Car entity = carRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Car not found: " + id));
        CarBrand brand = brandRepository.findById(request.getCarBrandId())
            .orElseThrow(() -> new IllegalArgumentException("CarBrand not found: " + request.getCarBrandId()));
        mapper.toEntity(request, entity);
        entity.setCarBrand(brand);
        return mapper.toResponse(carRepository.save(entity));
    }

    public CarResponse findById(Long id) {
        return carRepository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Car not found: " + id));
    }

    public List<CarResponse> findAll() {
        return carRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        carRepository.deleteById(id);
    }
}
