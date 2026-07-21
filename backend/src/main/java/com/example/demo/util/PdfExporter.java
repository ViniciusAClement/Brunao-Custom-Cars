package com.example.demo.util;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.dto.response.ReportResponse;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Component
public class PdfExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generateReportPdf(List<ReportResponse> reports) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph title = new Paragraph("Relatório de Vendas - Brunão Custom Cars")
                .setFont(titleFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Data de geração
        Paragraph generatedDate = new Paragraph("Gerado em: " + java.time.LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(generatedDate);
        document.add(new Paragraph("\n"));

        // Resumo
        Paragraph summary = new Paragraph("Resumo: " + reports.size() + " pedido(s) encontrado(s)")
                .setFontSize(11)
                .setTextAlignment(TextAlignment.LEFT);
        document.add(summary);
        document.add(new Paragraph("\n"));

        // Tabela
        float[] columnWidths = {1, 2, 2, 1.5f, 1.5f, 1.5f, 1.5f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Cabeçalho da tabela
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String[] headers = {"ID", "Cliente", "Email", "Valor", "Status", "Método", "Data"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setFont(headerFont))
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }

        // Linhas da tabela
        for (ReportResponse report : reports) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(report.getOrderId()))));
            table.addCell(new Cell().add(new Paragraph(report.getClientName() != null ? report.getClientName() : "")));
            table.addCell(new Cell().add(new Paragraph(report.getClientEmail() != null ? report.getClientEmail() : "")));
            table.addCell(new Cell().add(new Paragraph("R$ " + String.format("%.2f", report.getTotalValue() != null ? report.getTotalValue() : 0.0))));
            table.addCell(new Cell().add(new Paragraph(report.getOrderStatus() != null ? report.getOrderStatus() : "")));
            table.addCell(new Cell().add(new Paragraph(report.getPaymentMethod() != null ? report.getPaymentMethod() : "")));
            table.addCell(new Cell().add(new Paragraph(report.getCreatedAt() != null ? report.getCreatedAt().format(DATE_FORMATTER) : "")));
        }

        document.add(table);

        // Rodapé
        document.add(new Paragraph("\n"));
        Paragraph footer = new Paragraph("Documento confidencial - Apenas para uso interno")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }
}
