package com.chubb.inventoryapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {

    	if (secret == null || secret.trim().isEmpty()) {
            throw new RuntimeException("JWT Secret is missing in application.yml");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public void validateToken(String token) {
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}