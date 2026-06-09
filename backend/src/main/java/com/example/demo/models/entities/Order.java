package com.example.demo.models.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToOne
    @JoinColumn(name = "market_car_id", nullable = false)
    private MarketCar marketCar;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Double totalValue = 0.0;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Dados de pagamento
    private String transactionId; // ID da transação simulada
    
    // Para PIX
    private String pixKey;
    private String pixCode;
    
    // Para BOLETO
    private String boletoCode;
    private String barcode;
    
    // Para CARTÃO
    private String cardLastDigits;
    private String cardHolderName;

    public enum PaymentMethod {
        PIX,
        BOLETO,
        DEBIT_CARD,
        CREDIT_CARD
    }

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        PAID,
        FAILED,
        CANCELLED
    }
}
