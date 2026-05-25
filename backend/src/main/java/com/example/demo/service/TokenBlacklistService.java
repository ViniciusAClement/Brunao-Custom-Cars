package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.JwtUtil;
import com.example.demo.models.entities.BlacklistedToken;
import com.example.demo.repository.BlacklistedTokenRepository;

import io.jsonwebtoken.JwtException;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository repository;
    private final JwtUtil jwtUtil;
    private final long defaultExpirationMs;

    public TokenBlacklistService(
            BlacklistedTokenRepository repository,
            JwtUtil jwtUtil,
            @Value("${jwt.expiration:36000000}") long defaultExpirationMs) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
        this.defaultExpirationMs = defaultExpirationMs;
    }

    @Transactional
    public void blacklistToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        String tokenHash = hashToken(token);
        Instant expiresAt = resolveExpiration(token);

        if (repository.existsByTokenHashAndExpiresAtAfter(tokenHash, Instant.now())) {
            return;
        }

        BlacklistedToken entry = new BlacklistedToken();
        entry.setTokenHash(tokenHash);
        entry.setExpiresAt(expiresAt);
        repository.save(entry);
    }

    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        String tokenHash = hashToken(token);
        return repository.existsByTokenHashAndExpiresAtAfter(tokenHash, Instant.now());
    }

    @Scheduled(fixedRateString = "${jwt.blacklist.cleanup-interval-ms:3600000}")
    @Transactional
    public void purgeExpiredTokens() {
        repository.deleteByExpiresAtBefore(Instant.now());
    }

    private Instant resolveExpiration(String token) {
        try {
            Date expiration = jwtUtil.extractExpiration(token);
            return expiration.toInstant();
        } catch (JwtException ex) {
            return Instant.now().plusMillis(defaultExpirationMs);
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
