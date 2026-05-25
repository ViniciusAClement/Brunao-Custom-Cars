package com.example.demo.service;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.entities.Role;
import com.example.demo.models.entities.User;

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
        return create(request, false);
    }

    public ProductResponse create(ProductCreateRequest request, boolean isFuncionario) {
        Product entity = mapper.toEntity(request);
        if (isFuncionario) {
            entity.setPrice(null);
        } else if (entity.getPrice() == null) {
            throw new IllegalArgumentException("Price is required for gerente");
        }
        entity.setCategories(loadCategories(request.getCategoryIds()));
        entity.setCars(loadCars(request.getCarIds()));
        return mapper.toResponse(productRepository.save(entity));
    }

    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product entity = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        List<Category> categories = loadCategories(request.getCategoryIds());
        List<Car> cars = loadCars(request.getCarIds());
        mapper.toEntity(request, entity);
        entity.setCategories(categories);
        entity.setCars(cars);
        return mapper.toResponse(productRepository.save(entity));
    }

    public ProductResponse findById(Long id, Authentication authentication) {
        if (exposeOnlyPricedProducts(authentication)) {
            return productRepository.findByIdAndPriceIsNotNull(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        }
        return productRepository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public List<ProductResponse> findAll(Authentication authentication) {
        List<Product> products = exposeOnlyPricedProducts(authentication)
            ? productRepository.findByPriceIsNotNull()
            : productRepository.findAll();
        return products.stream().map(mapper::toResponse).toList();
    }

    public void ensureProductAvailableForSale(Product product) {
        if (product == null || product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException(
                "Produto indisponível para venda. O gerente ainda não definiu o preço."
            );
        }
    }

    public void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    public void validateStock(Product product, int quantity) {
        validateQuantity(quantity);
        int available = availableStock(product);
        if (quantity > available) {
            throw new IllegalArgumentException(
                "Quantidade solicitada excede o estoque disponível (" + available + ")"
            );
        }
    }

    /**
     * Reserva unidades no carrinho, decrementando o estoque disponível no banco.
     */
    public void reserveStock(Product product, int quantity) {
        validateStock(product, quantity);
        applyStockDelta(product, -quantity);
    }

    /**
     * Devolve unidades ao estoque quando itens saem do carrinho.
     */
    public void releaseStock(Product product, int quantity) {
        validateQuantity(quantity);
        applyStockDelta(product, quantity);
    }

    /**
     * Ajusta a reserva quando a quantidade de um item do carrinho muda.
     */
    public void adjustReservedStock(Product product, int previousQuantity, int newQuantity) {
        validateQuantity(newQuantity);
        int delta = newQuantity - previousQuantity;
        if (delta > 0) {
            validateStock(product, delta);
            applyStockDelta(product, -delta);
        } else if (delta < 0) {
            applyStockDelta(product, -delta);
        }
    }

    private int availableStock(Product product) {
        return product.getStock() != null ? product.getStock() : 0;
    }

    private void applyStockDelta(Product product, int delta) {
        Product managed = productRepository.findById(product.getId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + product.getId()));
        int stock = availableStock(managed);
        int newStock = stock + delta;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                "Quantidade solicitada excede o estoque disponível (" + stock + ")"
            );
        }
        managed.setStock(newStock);
        productRepository.save(managed);
        product.setStock(newStock);
    }

    private List<Category> loadCategories(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        ensureAllIdsResolved(categoryIds, categories.size(), "categories");
        return categories;
    }

    private List<Car> loadCars(List<Long> carIds) {
        List<Car> cars = carRepository.findAllById(carIds);
        ensureAllIdsResolved(carIds, cars.size(), "cars");
        return cars;
    }

    private void ensureAllIdsResolved(List<Long> requestedIds, int resolvedCount, String resourceLabel) {
        if (requestedIds == null || requestedIds.isEmpty()) {
            return;
        }
        long expectedDistinctIds = requestedIds.stream().distinct().count();
        if (resolvedCount != expectedDistinctIds) {
            throw new IllegalArgumentException("One or more " + resourceLabel + " were not found");
        }
    }

    private boolean exposeOnlyPricedProducts(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user.getRole() == Role.CLIENTE;
        }
        return true;
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
