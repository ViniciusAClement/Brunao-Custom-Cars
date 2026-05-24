package com.example.demo.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketCarResponse {

    private Long id;
    private Long clientId;
    private List<MarketCarItemResponse> items;
    private Double totalValue;
}
