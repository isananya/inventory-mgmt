package com.chubb.inventoryapp.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String TEST_SECRET = "superSecretKeyThatIsVeryLongAndSecureForTesting123!"; 

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET);
    }

    @Test
    void constructor_NullSecret_ThrowsException() {
        Exception ex = assertThrows(RuntimeException.class, () -> new JwtUtil(null));
        assertEquals("JWT Secret is missing in application.yml", ex.getMessage());
    }

    @Test
    void validateToken_ValidToken_NoException() {
        String token = generateTestToken("testUser");
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_InvalidSignature_ThrowsException() {
        String wrongSecret = "wrongSecretKeyThatIsVeryLongAndSecureForTesting123!";
        SecretKey wrongKey = Keys.hmacShaKeyFor(wrongSecret.getBytes());
        
        String invalidToken = Jwts.builder()
                .subject("testUser")
                .signWith(wrongKey)
                .compact();

        assertThrows(SignatureException.class, () -> jwtUtil.validateToken(invalidToken));
    }

    @Test
    void validateToken_MalformedToken_ThrowsException() {
        String garbageToken = "This.is.garbage";
        assertThrows(Exception.class, () -> jwtUtil.validateToken(garbageToken));
    }

    @Test
    void getAllClaimsFromToken_Success() {
        String token = generateTestToken("user@example.com");
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        assertEquals("user@example.com", claims.getSubject());
    }

    private String generateTestToken(String subject) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
        
        return Jwts.builder()
                .subject(subject)
                .signWith(key)
                .compact();
    }
}