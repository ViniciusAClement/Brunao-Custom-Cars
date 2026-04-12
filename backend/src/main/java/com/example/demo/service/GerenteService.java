package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.GerenteMapper;
import com.example.demo.dto.request.GerenteCreateRequest;
import com.example.demo.dto.request.GerenteUpdateRequest;
import com.example.demo.dto.response.GerenteResponse;
import com.example.demo.models.entities.Gerente;
import com.example.demo.repository.GerenteRepository;

@Service
@Transactional
public class GerenteService {

    private final GerenteRepository repository;
    private final GerenteMapper mapper;

    public GerenteService(GerenteRepository repository, GerenteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public GerenteResponse create(GerenteCreateRequest request) {
        Gerente entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public GerenteResponse update(Long id, GerenteUpdateRequest request) {
        Gerente entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Gerente not found: " + id));
        mapper.toEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public GerenteResponse findById(Long id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Gerente not found: " + id));
    }

    public List<GerenteResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
