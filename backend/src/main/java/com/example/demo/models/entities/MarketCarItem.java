package com.example.demo.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_car_items")
@Data
@NoArgsConstructor
public class MarketCarItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;
    private Double totalValue;

    @ManyToOne
    @JoinColumn(name = "market_car_id", nullable = false)
    private MarketCar marketCar;

    public MarketCarItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        recalculateTotalValue();
    }

    public void setProduct(Product product) {
        this.product = product;
        recalculateTotalValue();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        recalculateTotalValue();
    }

    public void recalculateTotalValue() {
        if (product == null || product.getPrice() == null || quantity == null) {
            this.totalValue = 0.0;
        } else {
            this.totalValue = product.getPrice() * quantity;
        }
    }
}
