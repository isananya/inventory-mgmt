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
    	System.out.println("==========================================");
        System.out.println("DEBUG: JWT Secret Length: " + (secret != null ? secret.length() : "null"));
        System.out.println("DEBUG: JWT Secret Value:  [" + secret + "]");
        System.out.println("==========================================");
        // Ensure key is long enough for HMAC-SHA algorithms
    	if (secret == null || secret.trim().isEmpty()) {
            throw new RuntimeException("JWT Secret is missing in application.yml");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Used by GlobalSecurityFilter to check validity.
     * Throws an exception if the token is invalid or expired.
     */
    public void validateToken(String token) {
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);
    }

    /**
     * Used by GlobalSecurityFilter to extract Role and Username.
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}