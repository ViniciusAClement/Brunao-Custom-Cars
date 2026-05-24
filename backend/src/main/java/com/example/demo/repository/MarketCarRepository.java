package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.entities.MarketCar;

@Repository
public interface MarketCarRepository extends JpaRepository<MarketCar, Long> {

    Optional<MarketCar> findByClientId(Long clientId);
}
