package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryResponse {
    
    private Long totalOrders;
    private Long paidOrders;
    private Long pendingOrders;
    private Long failedOrders;
    private Long cancelledOrders;
    private Double totalRevenue;
    private Double averageOrderValue;
    private Long totalClients;
    private Double percentageIncrease; // comparação com período anterior
}
