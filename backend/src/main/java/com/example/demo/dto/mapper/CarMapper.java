package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.CarCreateRequest;
import com.example.demo.dto.request.CarUpdateRequest;
import com.example.demo.dto.response.CarResponse;
import com.example.demo.models.entities.Car;

@Component
public class CarMapper {

    public CarResponse toResponse(Car entity) {
        if (entity == null) {
            return null;
        }
        String carBrandName = entity.getCarBrand() != null ? entity.getCarBrand().getName() : null;
        Long carBrandId = entity.getCarBrand() != null ? entity.getCarBrand().getId() : null;
        
        return new CarResponse(
            entity.getId(),
            entity.getNome(),
            entity.getAno(),
            carBrandId,
            carBrandName
        );
    }

    public Car toEntity(CarCreateRequest request) {
        if (request == null) {
            return null;
        }
        Car entity = new Car();
        entity.setNome(request.getNome());
        entity.setAno(request.getAno());
        return entity;
    }

    public Car toEntity(CarUpdateRequest request, Car entity) {
        if (request == null) {
            return entity;
        }
        entity.setNome(request.getNome());
        entity.setAno(request.getAno());
        return entity;
    }
}
