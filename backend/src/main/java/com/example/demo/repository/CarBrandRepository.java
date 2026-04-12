package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.entities.CarBrand;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
}
