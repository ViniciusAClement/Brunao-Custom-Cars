package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.FuncionarioCreateRequest;
import com.example.demo.dto.request.FuncionarioUpdateRequest;
import com.example.demo.dto.response.FuncionarioResponse;
import com.example.demo.service.FuncionarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public FuncionarioResponse create(@Valid @RequestBody FuncionarioCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<FuncionarioResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public FuncionarioResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public FuncionarioResponse update(@PathVariable Long id, @Valid @RequestBody FuncionarioUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
