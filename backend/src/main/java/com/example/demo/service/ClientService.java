package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.ClientMapper;
import com.example.demo.dto.request.ClientCreateRequest;
import com.example.demo.dto.request.ClientUpdateRequest;
import com.example.demo.dto.response.ClientResponse;
import com.example.demo.models.entities.Client;
import com.example.demo.repository.ClientRepository;

@Service
@Transactional
public class ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    public ClientService(ClientRepository repository, ClientMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ClientResponse create(ClientCreateRequest request) {
        Client entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public ClientResponse update(Long id, ClientUpdateRequest request) {
        Client entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + id));
        mapper.toEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public ClientResponse findById(Long id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + id));
    }

    public List<ClientResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
