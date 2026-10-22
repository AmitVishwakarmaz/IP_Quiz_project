package com.quizly.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void tokenCanBeVerifiedByTheSigningService() {
        JwtService jwtService = new JwtService("");
        String token = jwtService.createToken("teacher@example.com", "INSTRUCTOR");

        assertEquals("teacher@example.com", jwtService.extractEmail(token).orElseThrow());
        assertEquals("INSTRUCTOR", jwtService.extractRole(token).orElseThrow());
    }

    @Test
    void invalidTokenIsNotAccepted() {
        JwtService jwtService = new JwtService("");

        assertTrue(jwtService.extractEmail("not-a-jwt").isEmpty());
        assertTrue(jwtService.extractRole("not-a-jwt").isEmpty());
    }
}
