package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.CarBrandCreateRequest;
import com.example.demo.dto.request.CarBrandUpdateRequest;
import com.example.demo.dto.response.CarBrandResponse;
import com.example.demo.models.entities.CarBrand;

@Component
public class CarBrandMapper {

    public CarBrandResponse toResponse(CarBrand entity) {
        if (entity == null) {
            return null;
        }
        return new CarBrandResponse(entity.getId(), entity.getName());
    }

    public CarBrand toEntity(CarBrandCreateRequest request) {
        if (request == null) {
            return null;
        }
        CarBrand entity = new CarBrand();
        entity.setName(request.getName());
        return entity;
    }

    public CarBrand toEntity(CarBrandUpdateRequest request, CarBrand entity) {
        if (request == null) {
            return entity;
        }
        entity.setName(request.getName());
        return entity;
    }
}
