package com.quizly.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private static final long TOKEN_LIFETIME_SECONDS = 8 * 60 * 60;

    private final SecretKey signingKey;

    public JwtService(@Value("${JWT_SECRET:}") String configuredSecret) {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            signingKey = Jwts.SIG.HS256.key().build();
            return;
        }

        byte[] keyBytes = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must contain at least 32 bytes");
        }
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(TOKEN_LIFETIME_SECONDS)))
                .signWith(signingKey)
                .compact();
    }

    public Optional<String> extractEmail(String token) {
        return extractClaim(token, "sub");
    }

    public Optional<String> extractRole(String token) {
        return extractClaim(token, "role");
    }

    private Optional<String> extractClaim(String token, String claim) {
        try {
            Object value = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(claim);
            return value instanceof String stringValue ? Optional.of(stringValue) : Optional.empty();
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
