package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.AddressMapper;
import com.example.demo.dto.request.AddressCreateRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.models.entities.Address;
import com.example.demo.models.entities.Client;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.ClientRepository;

@Service
@Transactional
public class AddressService {

    private final AddressRepository repository;
    private final ClientRepository clientRepository;
    private final AddressMapper mapper;

    public AddressService(AddressRepository repository, ClientRepository clientRepository, AddressMapper mapper) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.mapper = mapper;
    }

    public AddressResponse create(AddressCreateRequest request) {
        Address entity = mapper.toEntity(request);
        Client client = clientRepository.findById(request.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + request.getClientId()));
        entity.setClient(client);
        return mapper.toResponse(repository.save(entity));
    }

    public AddressResponse update(Long id, AddressUpdateRequest request) {
        Address entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
        Client client = clientRepository.findById(request.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + request.getClientId()));
        mapper.toEntity(request, entity);
        entity.setClient(client);
        return mapper.toResponse(repository.save(entity));
    }

    public AddressResponse findById(Long id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
    }

    public List<AddressResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
