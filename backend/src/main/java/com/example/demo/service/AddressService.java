package com.example.demo.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.mapper.AddressMapper;
import com.example.demo.dto.request.AddressCreateRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.models.entities.Address;
import com.example.demo.models.entities.Client;
import com.example.demo.models.entities.Role;
import com.example.demo.models.entities.User;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.ClientRepository;

@Service
@Transactional
public class AddressService {

    private final AddressRepository repository;
    private final ClientRepository clientRepository;
    private final AddressMapper mapper;
    private final MarketCarAccessService accessService;

    public AddressService(
            AddressRepository repository,
            ClientRepository clientRepository,
            AddressMapper mapper,
            MarketCarAccessService accessService) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.mapper = mapper;
        this.accessService = accessService;
    }

    public AddressResponse create(AddressCreateRequest request, Authentication authentication) {
        Address entity = mapper.toEntity(request);
        Long clientId = accessService.resolveClientId(request.getClientId(), authentication);

        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        entity.setClient(client);
        return mapper.toResponse(repository.save(entity));
    }

    public AddressResponse update(Long id, AddressUpdateRequest request, Authentication authentication) {
        Address entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
        accessService.ensureCanAccessAddress(entity, authentication);

        Long clientId = accessService.resolveClientId(request.getClientId(), authentication);
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        mapper.toEntity(request, entity);
        entity.setClient(client);
        return mapper.toResponse(repository.save(entity));
    }

    public AddressResponse findById(Long id, Authentication authentication) {
        Address entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
        accessService.ensureCanAccessAddress(entity, authentication);
        return mapper.toResponse(entity);
    }

    public List<AddressResponse> findAll(Authentication authentication) {
        User user = accessService.requireAuthenticatedUser(authentication);
        if (user.getRole() == Role.CLIENTE) {
            return repository.findByClientId(user.getId()).stream().map(mapper::toResponse).toList();
        }
        accessService.ensureStaffOnly(authentication);
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id, Authentication authentication) {
        Address entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
        accessService.ensureCanAccessAddress(entity, authentication);
        repository.delete(entity);
    }
}
