package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarCreateRequest {

    @NotBlank(message = "Nome cannot be blank")
    @Size(min = 1, max = 100, message = "Nome must be between 1 and 100 characters")
    private String nome;

    @NotNull(message = "Ano cannot be null")
    @Min(value = 1900, message = "Ano must be at least 1900")
    private Integer ano;

    @NotNull(message = "CarBrandId cannot be null")
    private Long carBrandId;
}
