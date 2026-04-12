package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {

    @NotBlank(message = "Street cannot be blank")
    @Size(max = 200, message = "Street must be at most 200 characters")
    private String street;

    @NotBlank(message = "Number cannot be blank")
    @Size(max = 20, message = "Number must be at most 20 characters")
    private String number;

    @NotBlank(message = "Neighborhood cannot be blank")
    @Size(max = 100, message = "Neighborhood must be at most 100 characters")
    private String neighborhood;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State must be at most 100 characters")
    private String state;

    @NotBlank(message = "Zip code cannot be blank")
    @Size(max = 20, message = "Zip code must be at most 20 characters")
    private String zipCode;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;

    @NotNull(message = "ClientId cannot be null")
    private Long clientId;
}
