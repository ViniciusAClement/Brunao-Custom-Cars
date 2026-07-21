package com.example.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    
    private Long orderId;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private Long marketCarId;
    private Double totalValue;
    private String paymentMethod;
    private String orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String transactionId;
    
    // Dados de pagamento específicos
    private String pixKey;
    private String pixCode;
    private String boletoCode;
    private String barcode;
    private String cardLastDigits;
    private String cardHolderName;
}
