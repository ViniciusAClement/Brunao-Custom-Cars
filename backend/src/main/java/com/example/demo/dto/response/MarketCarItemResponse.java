package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketCarItemResponse {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Double totalValue;
}
