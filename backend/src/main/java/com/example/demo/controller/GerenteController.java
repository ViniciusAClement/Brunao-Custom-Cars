package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.GerenteCreateRequest;
import com.example.demo.dto.request.GerenteUpdateRequest;
import com.example.demo.dto.response.GerenteResponse;
import com.example.demo.service.GerenteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService service;

    public GerenteController(GerenteService service) {
        this.service = service;
    }

    @PostMapping
    public GerenteResponse create(@Valid @RequestBody GerenteCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<GerenteResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public GerenteResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public GerenteResponse update(@PathVariable Long id, @Valid @RequestBody GerenteUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
