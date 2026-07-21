package com.example.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.dto.response.ReportResponse;

@Component
public class CsvExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generateReportCsv(List<ReportResponse> reports) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new java.io.OutputStreamWriter(baos, StandardCharsets.UTF_8));

        // Cabeçalho
        writer.println("ID do Pedido,ID do Cliente,Nome do Cliente,Email,Valor Total,Status,Método de Pagamento,Data de Criação,ID da Transação,Chave PIX,Código PIX,Código Boleto,Código de Barras,Últimos Dígitos do Cartão,Titular do Cartão");

        // Linhas de dados
        for (ReportResponse report : reports) {
            StringBuilder line = new StringBuilder();
            line.append(escapeCSV(String.valueOf(report.getOrderId()))).append(",");
            line.append(escapeCSV(String.valueOf(report.getClientId()))).append(",");
            line.append(escapeCSV(report.getClientName())).append(",");
            line.append(escapeCSV(report.getClientEmail())).append(",");
            line.append(escapeCSV(String.format("%.2f", report.getTotalValue()))).append(",");
            line.append(escapeCSV(report.getOrderStatus())).append(",");
            line.append(escapeCSV(report.getPaymentMethod())).append(",");
            line.append(escapeCSV(report.getCreatedAt().format(DATE_FORMATTER))).append(",");
            line.append(escapeCSV(report.getTransactionId())).append(",");
            line.append(escapeCSV(report.getPixKey())).append(",");
            line.append(escapeCSV(report.getPixCode())).append(",");
            line.append(escapeCSV(report.getBoletoCode())).append(",");
            line.append(escapeCSV(report.getBarcode())).append(",");
            line.append(escapeCSV(report.getCardLastDigits())).append(",");
            line.append(escapeCSV(report.getCardHolderName()));

            writer.println(line.toString());
        }

        writer.flush();
        writer.close();

        return baos.toByteArray();
    }

    /**
     * Escapa valores CSV para evitar problemas com vírgulas e aspas
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
