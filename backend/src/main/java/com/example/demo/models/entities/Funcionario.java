package com.example.demo.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Funcionario extends User {
}
