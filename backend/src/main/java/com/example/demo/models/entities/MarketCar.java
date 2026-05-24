package com.example.demo.models.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @OneToMany(mappedBy = "marketCar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketCarItem> items = new ArrayList<>();

    private Double totalValue = 0.0;

    public void addItem(MarketCarItem item) {
        if (item == null) {
            return;
        }
        item.setMarketCar(this);
        items.add(item);
        recalculateTotalValue();
    }

    public void removeItem(MarketCarItem item) {
        if (item == null) {
            return;
        }
        items.remove(item);
        item.setMarketCar(null);
        recalculateTotalValue();
    }

    public void recalculateTotalValue() {
        this.totalValue = items.stream()
            .filter(i -> i != null && i.getTotalValue() != null)
            .mapToDouble(MarketCarItem::getTotalValue)
            .sum();
    }
}
