package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.GerenteCreateRequest;
import com.example.demo.dto.request.GerenteUpdateRequest;
import com.example.demo.dto.response.GerenteResponse;
import com.example.demo.models.entities.Gerente;
import com.example.demo.models.entities.Role;

@Component
public class GerenteMapper {

    public GerenteResponse toResponse(Gerente entity) {
        if (entity == null) {
            return null;
        }
        return new GerenteResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getCpf()
        );
    }

    public Gerente toEntity(GerenteCreateRequest request) {
        if (request == null) {
            return null;
        }
        Gerente entity = new Gerente();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setPassword(request.getPassword());
        entity.setCpf(request.getCpf());
        entity.setRole(Role.GERENTE);
        return entity;
    }

    public Gerente toEntity(GerenteUpdateRequest request, Gerente entity) {
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
