package com.example.demo.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.FuncionarioCreateRequest;
import com.example.demo.dto.request.FuncionarioUpdateRequest;
import com.example.demo.dto.response.FuncionarioResponse;
import com.example.demo.models.entities.Funcionario;
import com.example.demo.models.entities.Role;

@Component
public class FuncionarioMapper {

    public FuncionarioResponse toResponse(Funcionario entity) {
        if (entity == null) {
            return null;
        }
        return new FuncionarioResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getCpf()
        );
    }

    public Funcionario toEntity(FuncionarioCreateRequest request) {
        if (request == null) {
            return null;
        }
        Funcionario entity = new Funcionario();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setPassword(request.getPassword());
        entity.setCpf(request.getCpf());
        entity.setRole(Role.FUNCIONARIO);
        return entity;
    }

    public Funcionario toEntity(FuncionarioUpdateRequest request, Funcionario entity) {
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
