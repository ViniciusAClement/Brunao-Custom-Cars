package com.example.demo.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gerentes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Gerente extends User {
}
