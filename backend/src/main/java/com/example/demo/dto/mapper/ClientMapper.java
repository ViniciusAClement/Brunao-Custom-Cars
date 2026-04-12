package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.ClientCreateRequest;
import com.example.demo.dto.request.ClientUpdateRequest;
import com.example.demo.dto.response.ClientResponse;
import com.example.demo.models.entities.Client;
import com.example.demo.models.entities.Role;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client entity) {
        if (entity == null) {
            return null;
        }
        return new ClientResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getCpf()
        );
    }

    public Client toEntity(ClientCreateRequest request) {
        if (request == null) {
            return null;
        }
        Client entity = new Client();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setPassword(request.getPassword());
        entity.setCpf(request.getCpf());
        entity.setRole(Role.CLIENTE);
        return entity;
    }

    public Client toEntity(ClientUpdateRequest request, Client entity) {
        if (request == null) {
            return entity;
        }
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setPassword(request.getPassword());
        entity.setCpf(request.getCpf());
        return entity;
    }
}
