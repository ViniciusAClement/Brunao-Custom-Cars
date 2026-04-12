package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.ProductCreateRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.mapper.ProductMapper;
import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.Category;
import com.example.demo.models.entities.Product;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            CarRepository carRepository,
            ProductMapper mapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.carRepository = carRepository;
        this.mapper = mapper;
    }

    public ProductResponse create(ProductCreateRequest request) {
        Product entity = mapper.toEntity(request);
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        List<Car> cars = carRepository.findAllById(request.getCarIds());
        entity.setCategories(categories);
        entity.setCars(cars);
        return mapper.toResponse(productRepository.save(entity));
    }

    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product entity = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        List<Car> cars = carRepository.findAllById(request.getCarIds());
        mapper.toEntity(request, entity);
        entity.setCategories(categories);
        entity.setCars(cars);
        return mapper.toResponse(productRepository.save(entity));
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
