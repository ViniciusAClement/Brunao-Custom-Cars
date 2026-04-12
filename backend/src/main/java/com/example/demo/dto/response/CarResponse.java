package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {

    private Long id;

    private String nome;

    private Integer ano;

    private Long carBrandId;

    private String carBrandName;
}
