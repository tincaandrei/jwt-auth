package com.example.demo.services;

import com.example.demo.entities.RefreshToken;
import com.example.demo.entities.User;
import com.example.demo.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public String issue(User user, long ttlDays) {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(ttlDays, ChronoUnit.DAYS);
        String rawToken;
        String tokenHash;
        do {
            rawToken = generateRawToken();
            tokenHash = hash(rawToken);
        } while (refreshTokenRepository.findByTokenHash(tokenHash).isPresent());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional(readOnly = true)
    public User validateAndGetUser(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        String tokenHash = hash(rawToken);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));

        Instant now = Instant.now(clock);
        if (refreshToken.isRevoked() || !refreshToken.getExpiresAt().isAfter(now)) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        return refreshToken.getUser();
    }

    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        String tokenHash = hash(rawToken);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));
        if (!refreshToken.isRevoked()) {
            refreshToken.setRevoked(true);
            refreshToken.setRevokedAt(Instant.now(clock));
        }
    }

    @Transactional
    public void revokeAllForUser(User user) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
        Instant now = Instant.now(clock);
        for (RefreshToken token : activeTokens) {
            token.setRevoked(true);
            token.setRevokedAt(now);
        }
    }

    private String generateRawToken() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HEX_FORMAT.formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
