package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long clientId);
    Optional<Order> findByMarketCarId(Long marketCarId);
    
    // Queries para relatórios
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersByStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.paymentMethod = :paymentMethod ORDER BY o.createdAt DESC")
    List<Order> findOrdersByPaymentMethod(@Param("paymentMethod") Order.PaymentMethod paymentMethod);
    
    @Query("SELECT o FROM Order o WHERE o.client.id = :clientId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByClientAndDateRange(@Param("clientId") Long clientId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByStatusAndDateRange(@Param("status") Order.OrderStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o")
    Long countTotalOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PAID'")
    Long countPaidOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    Long countPendingOrders();
    
    @Query("SELECT SUM(o.totalValue) FROM Order o WHERE o.status = 'PAID'")
    Double sumTotalRevenue();
    
    @Query("SELECT SUM(o.totalValue) FROM Order o WHERE o.status = 'PAID' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double sumRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
