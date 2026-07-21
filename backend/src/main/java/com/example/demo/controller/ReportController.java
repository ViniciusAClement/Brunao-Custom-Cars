package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.ReportResponse;
import com.example.demo.dto.response.ReportSummaryResponse;
import com.example.demo.service.ReportService;
import com.example.demo.util.CsvExporter;
import com.example.demo.util.PdfExporter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GERENTE')")
public class ReportController {

    private final ReportService reportService;
    private final PdfExporter pdfExporter;
    private final CsvExporter csvExporter;

    /**
     * Obtém todos os pedidos
     */
    @GetMapping("/orders")
    public ResponseEntity<List<ReportResponse>> getAllOrders() {
        List<ReportResponse> orders = reportService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Obtém pedidos por período
     */
    @GetMapping("/orders/date-range")
    public ResponseEntity<List<ReportResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<ReportResponse> orders = reportService.getOrdersByDateRange(start, end);
        return ResponseEntity.ok(orders);
    }

    /**
     * Obtém pedidos por status
     */
    @GetMapping("/orders/status")
    public ResponseEntity<List<ReportResponse>> getOrdersByStatus(
            @RequestParam String status) {
        
        List<ReportResponse> orders = reportService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Obtém pedidos por método de pagamento
     */
    @GetMapping("/orders/payment-method")
    public ResponseEntity<List<ReportResponse>> getOrdersByPaymentMethod(
            @RequestParam String paymentMethod) {
        
        List<ReportResponse> orders = reportService.getOrdersByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(orders);
    }

    /**
     * Obtém pedidos por cliente
     */
    @GetMapping("/orders/client")
    public ResponseEntity<List<ReportResponse>> getOrdersByClient(
            @RequestParam Long clientId) {
        
        List<ReportResponse> orders = reportService.getOrdersByClient(clientId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Obtém pedidos por cliente e período
     */
    @GetMapping("/orders/client-date-range")
    public ResponseEntity<List<ReportResponse>> getOrdersByClientAndDateRange(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<ReportResponse> orders = reportService.getOrdersByClientAndDateRange(clientId, start, end);
        return ResponseEntity.ok(orders);
    }

    /**
     * Obtém resumo geral de vendas
     */
    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponse> getSalesSummary() {
        ReportSummaryResponse summary = reportService.getSalesSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Obtém resumo de vendas por período
     */
    @GetMapping("/summary/date-range")
    public ResponseEntity<ReportSummaryResponse> getSalesSummaryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        ReportSummaryResponse summary = reportService.getSalesSummaryByDateRange(start, end);
        return ResponseEntity.ok(summary);
    }

    /**
     * Exporta pedidos em PDF
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportOrdersToPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<ReportResponse> orders;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            orders = reportService.getOrdersByDateRange(start, end);
        } else {
            orders = reportService.getAllOrders();
        }
        
        try {
            byte[] pdfContent = pdfExporter.generateReportPdf(orders);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_vendas.pdf");
            headers.setContentLength(pdfContent.length);
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporta pedidos em CSV
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportOrdersToCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<ReportResponse> orders;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            orders = reportService.getOrdersByDateRange(start, end);
        } else {
            orders = reportService.getAllOrders();
        }
        
        try {
            byte[] csvContent = csvExporter.generateReportCsv(orders);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "relatorio_vendas.csv");
            headers.setContentLength(csvContent.length);
            
            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
