package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    private Long marketCarId;
    private String paymentMethod; // PIX, BOLETO, DEBIT_CARD, CREDIT_CARD
    
    // Dados PIX
    private String pixKey;
    
    // Dados BOLETO
    private String boletoEmail;
    
    // Dados CARTÃO
    private String cardNumber;
    private String cardHolderName;
    private String expirationDate; // MM/YY
    private String cvv;
    private Integer installments; // 1-12
}
