package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.ReportResponse;
import com.example.demo.dto.response.ReportSummaryResponse;
import com.example.demo.models.entities.Client;
import com.example.demo.models.entities.Order;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    /**
     * Obtém todos os pedidos com conversão para ReportResponse
     */
    public List<ReportResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
            .map(this::convertToReportResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtém pedidos por período
     */
    public List<ReportResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findOrdersByDateRange(startDate, endDate);
        return orders.stream()
            .map(this::convertToReportResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtém pedidos por status
     */
    public List<ReportResponse> getOrdersByStatus(String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findOrdersByStatus(orderStatus);
            return orders.stream()
                .map(this::convertToReportResponse)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    /**
     * Obtém pedidos por método de pagamento
     */
    public List<ReportResponse> getOrdersByPaymentMethod(String paymentMethod) {
        try {
            Order.PaymentMethod method = Order.PaymentMethod.valueOf(paymentMethod.toUpperCase());
            List<Order> orders = orderRepository.findOrdersByPaymentMethod(method);
            return orders.stream()
                .map(this::convertToReportResponse)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    /**
     * Obtém pedidos por cliente
     */
    public List<ReportResponse> getOrdersByClient(Long clientId) {
        List<Order> orders = orderRepository.findByClientId(clientId);
        return orders.stream()
            .map(this::convertToReportResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtém pedidos por cliente e período
     */
    public List<ReportResponse> getOrdersByClientAndDateRange(Long clientId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findOrdersByClientAndDateRange(clientId, startDate, endDate);
        return orders.stream()
            .map(this::convertToReportResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtém pedidos por status e período
     */
    public List<ReportResponse> getOrdersByStatusAndDateRange(String status, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findOrdersByStatusAndDateRange(orderStatus, startDate, endDate);
            return orders.stream()
                .map(this::convertToReportResponse)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    /**
     * Obtém resumo geral de vendas
     */
    public ReportSummaryResponse getSalesSummary() {
        ReportSummaryResponse summary = new ReportSummaryResponse();
        
        summary.setTotalOrders(orderRepository.countTotalOrders() != null ? orderRepository.countTotalOrders() : 0L);
        summary.setPaidOrders(orderRepository.countPaidOrders() != null ? orderRepository.countPaidOrders() : 0L);
        summary.setPendingOrders(orderRepository.countPendingOrders() != null ? orderRepository.countPendingOrders() : 0L);
        
        Long failedAndCancelled = summary.getTotalOrders() - summary.getPaidOrders() - summary.getPendingOrders();
        summary.setFailedOrders(failedAndCancelled / 2);
        summary.setCancelledOrders(failedAndCancelled / 2);
        
        Double revenue = orderRepository.sumTotalRevenue();
        summary.setTotalRevenue(revenue != null ? revenue : 0.0);
        
        if (summary.getTotalOrders() > 0) {
            summary.setAverageOrderValue(summary.getTotalRevenue() / summary.getTotalOrders());
        } else {
            summary.setAverageOrderValue(0.0);
        }
        
        summary.setTotalClients((long) clientRepository.findAll().size());
        summary.setPercentageIncrease(0.0);
        
        return summary;
    }

    /**
     * Obtém resumo de vendas por período
     */
    public ReportSummaryResponse getSalesSummaryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        ReportSummaryResponse summary = new ReportSummaryResponse();
        
        List<Order> orders = orderRepository.findOrdersByDateRange(startDate, endDate);
        
        summary.setTotalOrders((long) orders.size());
        summary.setPaidOrders(orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PAID).count());
        summary.setPendingOrders(orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count());
        summary.setFailedOrders(orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.FAILED).count());
        summary.setCancelledOrders(orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.CANCELLED).count());
        
        Double revenue = orderRepository.sumRevenueByDateRange(startDate, endDate);
        summary.setTotalRevenue(revenue != null ? revenue : 0.0);
        
        if (summary.getTotalOrders() > 0) {
            summary.setAverageOrderValue(summary.getTotalRevenue() / summary.getTotalOrders());
        } else {
            summary.setAverageOrderValue(0.0);
        }
        
        // Calcular aumento percentual comparado ao período anterior
        LocalDateTime previousStartDate = startDate.minusMonths(1);
        LocalDateTime previousEndDate = endDate.minusMonths(1);
        Double previousRevenue = orderRepository.sumRevenueByDateRange(previousStartDate, previousEndDate);
        previousRevenue = previousRevenue != null ? previousRevenue : 0.0;
        
        if (previousRevenue > 0) {
            summary.setPercentageIncrease(((summary.getTotalRevenue() - previousRevenue) / previousRevenue) * 100);
        } else {
            summary.setPercentageIncrease(0.0);
        }
        
        summary.setTotalClients((long) clientRepository.findAll().size());
        
        return summary;
    }

    /**
     * Converte Order para ReportResponse
     */
    private ReportResponse convertToReportResponse(Order order) {
        ReportResponse response = new ReportResponse();
        
        response.setOrderId(order.getId());
        response.setClientId(order.getClient().getId());
        response.setClientName(order.getClient().getName());
        response.setClientEmail(order.getClient().getEmail());
        response.setMarketCarId(order.getMarketCar().getId());
        response.setTotalValue(order.getTotalValue());
        response.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null);
        response.setOrderStatus(order.getStatus() != null ? order.getStatus().toString() : null);
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setTransactionId(order.getTransactionId());
        
        // Dados específicos de pagamento
        response.setPixKey(order.getPixKey());
        response.setPixCode(order.getPixCode());
        response.setBoletoCode(order.getBoletoCode());
        response.setBarcode(order.getBarcode());
        response.setCardLastDigits(order.getCardLastDigits());
        response.setCardHolderName(order.getCardHolderName());
        
        return response;
    }
}
