package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CategoryCreateRequest;
import com.example.demo.dto.request.CategoryUpdateRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.mapper.CategoryMapper;
import com.example.demo.models.entities.Category;
import com.example.demo.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CategoryResponse create(CategoryCreateRequest request) {
        Category entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        mapper.toEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public CategoryResponse findById(Long id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    public List<CategoryResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
