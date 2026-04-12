package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.entities.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
}
