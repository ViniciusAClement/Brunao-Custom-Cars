package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;

    private String street;

    private String number;

    private String neighborhood;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private Long clientId;
}
