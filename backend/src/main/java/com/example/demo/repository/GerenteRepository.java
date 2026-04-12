package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.entities.Gerente;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
}
