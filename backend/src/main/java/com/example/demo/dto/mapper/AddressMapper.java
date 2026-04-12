package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.AddressCreateRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.models.entities.Address;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address entity) {
        if (entity == null) {
            return null;
        }
        Long clientId = entity.getClient() != null ? entity.getClient().getId() : null;
        return new AddressResponse(
            entity.getId(),
            entity.getStreet(),
            entity.getNumber(),
            entity.getNeighborhood(),
            entity.getCity(),
            entity.getState(),
            entity.getZipCode(),
            entity.getCountry(),
            clientId
        );
    }

    public Address toEntity(AddressCreateRequest request) {
        if (request == null) {
            return null;
        }
        Address entity = new Address();
        entity.setStreet(request.getStreet());
        entity.setNumber(request.getNumber());
        entity.setNeighborhood(request.getNeighborhood());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setZipCode(request.getZipCode());
        entity.setCountry(request.getCountry());
        return entity;
    }

    public Address toEntity(AddressUpdateRequest request, Address entity) {
        if (request == null) {
            return entity;
        }
        entity.setStreet(request.getStreet());
        entity.setNumber(request.getNumber());
        entity.setNeighborhood(request.getNeighborhood());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setZipCode(request.getZipCode());
        entity.setCountry(request.getCountry());
        return entity;
    }
}
