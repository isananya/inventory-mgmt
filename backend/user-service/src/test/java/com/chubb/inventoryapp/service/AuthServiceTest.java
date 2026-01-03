package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chubb.inventoryapp.dto.ChangePasswordRequest;
import com.chubb.inventoryapp.dto.LoginRequest;
import com.chubb.inventoryapp.dto.LoginResponse;
import com.chubb.inventoryapp.dto.SignupRequest;
import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.exception.PasswordMismatchException;
import com.chubb.inventoryapp.exception.UserAlreadyExistsException;
import com.chubb.inventoryapp.exception.UserInactiveException;
import com.chubb.inventoryapp.exception.UserNotFoundException;
import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;
import com.chubb.inventoryapp.repository.UserRepository;
import com.chubb.inventoryapp.security.JwtService;
import com.chubb.inventoryapp.security.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private AuthService authService;

    private User user;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ananya Nayak");
        user.setEmail("user@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        signupRequest = new SignupRequest("user@gmail.com", "password123", "Ananya Nayak", Role.CUSTOMER);
    }

    @Test
    void register_NewUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.register(signupRequest);

        assertNotNull(result);
        assertEquals("Ananya Nayak", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserExistsAndActive_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(signupRequest));
    }

    @Test
    void register_UserExistsAndInactive_ReactivatesUser() {
        user.setActive(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(signupRequest);

        assertTrue(result.isActive());
        assertEquals("newEncodedPassword", result.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("user@gmail.com", "password123");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user); // Active user
        
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER"));
        doReturn(authorities).when(userDetails).getAuthorities();
        
        when(jwtService.generateToken(userDetails)).thenReturn("dummy-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getCookie());
        assertEquals("dummy-jwt-token", response.getCookie().getValue());
        assertEquals("Ananya Nayak", response.getBody().get("name"));
    }

    @Test
    void login_InactiveUser_ThrowsException() {
        LoginRequest loginRequest = new LoginRequest("user@gmail.com", "password123");
        user.setActive(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);

        assertThrows(UserInactiveException.class, () -> authService.login(loginRequest));
    }

    @Test
    void getProfile_Success() {
        String token = "valid-token";
        when(jwtService.extractUsername(token)).thenReturn("user@gmail.com");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));

        UserProfileResponse response = authService.getProfile(token);

        assertEquals("Ananya Nayak", response.getName());
        assertEquals("user@gmail.com", response.getEmail());
    }

    @Test
    void getProfile_UserNotFound_ThrowsException() {
        String token = "valid-token";
        when(jwtService.extractUsername(token)).thenReturn("unknown@gmail.com");
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.getProfile(token));
    }

    @Test
    void logout_Success() {
        ResponseCookie cookie = authService.logout();
        assertEquals(0, cookie.getMaxAge().getSeconds());
        assertEquals("", cookie.getValue());
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        String token = "token";
        
        when(jwtService.extractUsername(token)).thenReturn("user@gmail.com");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedHash");

        authService.changePassword(token, request);

        assertEquals("newEncodedHash", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_Mismatch_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPass", "newPass");
        String token = "token";
        
        when(jwtService.extractUsername(token)).thenReturn("user@gmail.com");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        assertThrows(PasswordMismatchException.class, () -> authService.changePassword(token, request));
    }
    
    @Test
    void deleteAccount_Success() {
        String token = "token";
        when(jwtService.extractUsername(token)).thenReturn("user@gmail.com");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));

        authService.deleteAccount(token);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }
}
