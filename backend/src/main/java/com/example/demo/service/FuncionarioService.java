package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.FuncionarioMapper;
import com.example.demo.dto.request.FuncionarioCreateRequest;
import com.example.demo.dto.request.FuncionarioUpdateRequest;
import com.example.demo.dto.response.FuncionarioResponse;
import com.example.demo.models.entities.Funcionario;
import com.example.demo.repository.FuncionarioRepository;

@Service
@Transactional
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;

    public FuncionarioService(FuncionarioRepository repository, FuncionarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public FuncionarioResponse create(FuncionarioCreateRequest request) {
        Funcionario entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public FuncionarioResponse update(Long id, FuncionarioUpdateRequest request) {
        Funcionario entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Funcionario not found: " + id));
        mapper.toEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public FuncionarioResponse findById(Long id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Funcionario not found: " + id));
    }

    public List<FuncionarioResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
