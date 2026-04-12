package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.CategoryCreateRequest;
import com.example.demo.dto.request.CategoryUpdateRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.models.entities.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) {
            return null;
        }
        return new CategoryResponse(entity.getId(), entity.getName());
    }

    public Category toEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }
        Category entity = new Category();
        entity.setName(request.getName());
        return entity;
    }

    public Category toEntity(CategoryUpdateRequest request, Category entity) {
        if (request == null) {
            return entity;
        }
        entity.setName(request.getName());
        return entity;
    }
}
