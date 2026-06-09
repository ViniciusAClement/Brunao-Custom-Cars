package com.example.demo.dto.response;

import com.example.demo.models.entities.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long orderId;
    private String paymentMethod;
    private String status; // PENDING, PROCESSING, PAID, FAILED
    private Double totalValue;
    private String transactionId;
    
    // Dados PIX
    private String pixCode;
    private String pixKey;
    
    // Dados BOLETO
    private String boletoCode;
    private String barcode;
    
    // Dados CARTÃO
    private String cardLastDigits;
    private String cardHolderName;
    
    private String message;

    public static PaymentResponse fromOrder(Order order) {
        return new PaymentResponse(
            order.getId(),
            order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null,
            order.getStatus() != null ? order.getStatus().toString() : null,
            order.getTotalValue(),
            order.getTransactionId(),
            order.getPixCode(),
            order.getPixKey(),
            order.getBoletoCode(),
            order.getBarcode(),
            order.getCardLastDigits(),
            order.getCardHolderName(),
            "Pagamento processado com sucesso!"
        );
    }
}
