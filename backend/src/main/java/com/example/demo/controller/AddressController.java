package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.AddressCreateRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE','GERENTE')")
    public AddressResponse create(@Valid @RequestBody AddressCreateRequest request, Authentication authentication) {
        return service.create(request, authentication);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','GERENTE','FUNCIONARIO')")
    public List<AddressResponse> findAll(Authentication authentication) {
        return service.findAll(authentication);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','GERENTE','FUNCIONARIO')")
    public AddressResponse findById(@PathVariable Long id, Authentication authentication) {
        return service.findById(id, authentication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','GERENTE')")
    public AddressResponse update(@PathVariable Long id, @Valid @RequestBody AddressUpdateRequest request,
            Authentication authentication) {
        return service.update(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','GERENTE')")
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, authentication);
    }
}
