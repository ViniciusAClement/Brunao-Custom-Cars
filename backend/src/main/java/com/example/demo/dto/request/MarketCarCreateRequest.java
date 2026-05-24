package com.example.demo.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketCarCreateRequest {

    @NotNull(message = "ClientId cannot be null")
    private Long clientId;

    private List<MarketCarItemCreateRequest> items;
}
