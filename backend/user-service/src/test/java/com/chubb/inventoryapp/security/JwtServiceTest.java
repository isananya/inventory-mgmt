package com.chubb.inventoryapp.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    
    private final String secret = "12345678901234567890123456789012"; 
    private final long expirationMs = 1000 * 60 * 60;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expirationMs);
    }

    @Test
    void generateToken_Success() {
        when(userDetails.getUsername()).thenReturn("test@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.CUSTOMER);

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_Success() {
        when(userDetails.getUsername()).thenReturn("user@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.ADMIN);

        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("user@gmail.com", username);
    }

    @Test
    void extractRole_Success() {
        when(userDetails.getUsername()).thenReturn("admin@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.ADMIN);

        String token = jwtService.generateToken(userDetails);
        String role = jwtService.extractRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void isTokenValid_Success() {
        when(userDetails.getUsername()).thenReturn("valid@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.CUSTOMER);

        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_UsernameMismatch_ReturnsFalse() {
        when(userDetails.getUsername()).thenReturn("user1@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("user2@gmail.com");

        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken_ThrowsException() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String expiredToken = Jwts.builder()
                .subject("expired@gmail.com")
                .claim("role", "CUSTOMER")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
    }

    @Test
    void extractAllClaims_InvalidSignature_ThrowsException() {
        when(userDetails.getUsername()).thenReturn("hacker@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        
        String token = jwtService.generateToken(userDetails);

        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + "tampered" + "." + parts[2];

        assertThrows(Exception.class, () -> jwtService.extractUsername(tamperedToken));
    }
    
    @Test
    void extractClaim_ExpirationDate_Success() {
        when(userDetails.getUsername()).thenReturn("time@gmail.com");
        when(userDetails.getUser()).thenReturn(user);
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        
        String token = jwtService.generateToken(userDetails);
        
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
}