package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByClientId(Long clientId);
}
