package com.example.demo.dto.request;

import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 150, message = "Name must be between 1 and 150 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @NotEmpty(message = "At least one category must be selected")
    private List<Long> categoryIds;

    @NotEmpty(message = "At least one car must be selected")
    private List<Long> carIds;
}
