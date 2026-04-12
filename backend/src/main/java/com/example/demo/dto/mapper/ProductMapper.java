package com.example.demo.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.ProductCreateRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.models.entities.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }
        
        List<Long> categoryIds = entity.getCategories() != null
            ? entity.getCategories().stream().map(c -> c.getId()).collect(Collectors.toList())
            : null;
            
        List<Long> carIds = entity.getCars() != null
            ? entity.getCars().stream().map(c -> c.getId()).collect(Collectors.toList())
            : null;
        
        return new ProductResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            categoryIds,
            carIds
        );
    }

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }
        Product entity = new Product();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        return entity;
    }

    public Product toEntity(ProductUpdateRequest request, Product entity) {
        if (request == null) {
            return entity;
        }
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        return entity;
    }
}
