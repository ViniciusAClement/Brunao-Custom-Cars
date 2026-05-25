package com.example.demo.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.entities.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByTokenHashAndExpiresAtAfter(String tokenHash, Instant instant);

    @Modifying
    @Transactional
    int deleteByExpiresAtBefore(Instant instant);
}
