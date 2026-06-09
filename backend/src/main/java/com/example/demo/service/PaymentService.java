package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.PaymentRequest;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.models.entities.Order;
import com.example.demo.models.entities.MarketCar;
import com.example.demo.models.entities.Order.OrderStatus;
import com.example.demo.models.entities.Order.PaymentMethod;
import com.example.demo.repository.MarketCarRepository;
import com.example.demo.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MarketCarRepository marketCarRepository;

    public PaymentService(OrderRepository orderRepository, MarketCarRepository marketCarRepository) {
        this.orderRepository = orderRepository;
        this.marketCarRepository = marketCarRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, Long clientId) {
        log.info("Processando pagamento para cliente: {}, método: {}", clientId, request.getPaymentMethod());

        // Validar MarketCar
        Optional<MarketCar> marketCarOpt = marketCarRepository.findById(request.getMarketCarId());
        if (marketCarOpt.isEmpty()) {
            throw new IllegalArgumentException("Carrinho não encontrado");
        }

        MarketCar marketCar = marketCarOpt.get();

        // Validar se o carrinho pertence ao cliente
        if (!marketCar.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("Carrinho não pertence ao cliente autenticado");
        }

        // Validar se o carrinho tem itens
        if (marketCar.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrinho vazio. Não é possível processar pagamento");
        }

        // Validar valor total
        if (marketCar.getTotalValue() == null || marketCar.getTotalValue() <= 0) {
            throw new IllegalArgumentException("Valor total inválido");
        }

        // Criar ou atualizar pedido
        Optional<Order> existingOrder = orderRepository.findByMarketCarId(request.getMarketCarId());
        Order order = existingOrder.orElseGet(() -> {
            Order newOrder = new Order();
            newOrder.setClient(marketCar.getClient());
            newOrder.setMarketCar(marketCar);
            newOrder.setTotalValue(marketCar.getTotalValue());
            return newOrder;
        });

        // Processar pagamento baseado no método
        PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.PROCESSING);
        order.setUpdatedAt(LocalDateTime.now());

        switch (paymentMethod) {
            case PIX:
                processPixPayment(order, request);
                break;
            case BOLETO:
                processBoletoPayment(order, request);
                break;
            case DEBIT_CARD:
                processCardPayment(order, request, "DEBIT");
                break;
            case CREDIT_CARD:
                processCardPayment(order, request, "CREDIT");
                break;
            default:
                throw new IllegalArgumentException("Método de pagamento não suportado: " + paymentMethod);
        }

        // Simular sucesso (95% de chance)
        boolean paymentSuccess = Math.random() > 0.05;
        
        if (paymentSuccess) {
            order.setStatus(OrderStatus.PAID);
            order.setTransactionId("TXN-" + UUID.randomUUID().toString());
            log.info("Pagamento bem-sucedido. Transação: {}", order.getTransactionId());
        } else {
            order.setStatus(OrderStatus.FAILED);
            log.warn("Pagamento falhado para MarketCar: {}", request.getMarketCarId());
        }

        Order savedOrder = orderRepository.save(order);
        PaymentResponse response = PaymentResponse.fromOrder(savedOrder);
        response.setMessage(paymentSuccess ? "Pagamento realizado com sucesso!" : "Falha no processamento do pagamento. Tente novamente.");
        
        return response;
    }

    private void processPixPayment(Order order, PaymentRequest request) {
        log.info("Processando pagamento PIX");
        
        // Validar PIX key
        if (request.getPixKey() == null || request.getPixKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Chave PIX obrigatória");
        }

        // Simular geração de código PIX
        order.setPixKey(request.getPixKey());
        order.setPixCode("00020126360014br.gov.bcb.pix0136" + UUID.randomUUID().toString().substring(0, 20).toUpperCase() + "520400005303986540510.005802BR5913Brunao Custom6009Sao Paulo62410503***63041D3D");
    }

    private void processBoletoPayment(Order order, PaymentRequest request) {
        log.info("Processando pagamento BOLETO");
        
        // Simular geração de código de boleto
        String boletoCode = String.format("%s.%s %s.%s %s.%s %s %s",
            "33791", "79001", generateRandomDigits(5), generateRandomDigits(5),
            generateRandomDigits(5), generateRandomDigits(5),
            generateVerifierDigit(), generateRandomDigits(14));
        
        order.setBoletoCode(boletoCode);
        order.setBarcode(generateBarcodeNumber());
    }

    private void processCardPayment(Order order, PaymentRequest request, String cardType) {
        log.info("Processando pagamento com CARTÃO ({})", cardType);
        
        // Validar dados do cartão
        if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Número do cartão obrigatório");
        }
        
        if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do titular obrigatório");
        }
        
        if (request.getExpirationDate() == null || request.getExpirationDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Data de expiração obrigatória");
        }
        
        if (request.getCvv() == null || request.getCvv().trim().isEmpty()) {
            throw new IllegalArgumentException("CVV obrigatório");
        }

        // Armazenar últimos 4 dígitos do cartão (simular segurança)
        String lastDigits = request.getCardNumber().substring(Math.max(0, request.getCardNumber().length() - 4));
        order.setCardLastDigits(lastDigits);
        order.setCardHolderName(request.getCardHolderName());
    }

    private String generateRandomDigits(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    private String generateVerifierDigit() {
        return String.valueOf((int) (Math.random() * 10));
    }

    private String generateBarcodeNumber() {
        return "33791" + generateRandomDigits(34);
    }

    public PaymentResponse getOrderStatus(Long orderId, Long clientId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (!order.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("Pedido não pertence ao cliente autenticado");
        }

        return PaymentResponse.fromOrder(order);
    }

    public Optional<Order> getOrderByMarketCar(Long marketCarId) {
        return orderRepository.findByMarketCarId(marketCarId);
    }
}
