package com.chubb.inventoryapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chubb.inventoryapp.dto.ChangePasswordRequest;
import com.chubb.inventoryapp.dto.LoginRequest;
import com.chubb.inventoryapp.dto.LoginResponse;
import com.chubb.inventoryapp.dto.SignupRequest;
import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;
import com.chubb.inventoryapp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void signup_Success() throws Exception {
        SignupRequest request = new SignupRequest("user@gmail.com", "password123", "Ananya Nayak", Role.CUSTOMER);
        User user = new User();
        user.setId(1L);
        
        when(authService.register(any(SignupRequest.class))).thenReturn(user);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("user@gmail.com", "password123");
        ResponseCookie cookie = ResponseCookie.from("jwt_token", "token-val").build();
        Map<String, Object> body = new HashMap<>();
        body.put("email", "user@gmail.com");
        
        LoginResponse loginResponse = new LoginResponse(cookie, body);
        
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", "jwt_token=token-val"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));
    }

    @Test
    void logout_Success() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("jwt_token", "").maxAge(0).build();
        when(authService.logout()).thenReturn(cookie);

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", "jwt_token=; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT"));
    }

    @Test
    void getProfile_Success() throws Exception {
        UserProfileResponse profile = new UserProfileResponse(1L, "Ananya Nayak", "user@gmail.com", "CUSTOMER", true);
        
        when(authService.getProfile(anyString())).thenReturn(profile);

        mockMvc.perform(get("/auth/profile")
                .cookie(new Cookie("jwt_token", "valid-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ananya Nayak"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass8Ch");
        
        doNothing().when(authService).changePassword(anyString(), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/auth/password")
                .cookie(new Cookie("jwt_token", "valid-token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void deleteAccount_Success() throws Exception {
    	ResponseCookie cookie = ResponseCookie.from("jwt_token", "").maxAge(0).build();
    	
    	doNothing().when(authService).deleteAccount(anyString());
    	when(authService.logout()).thenReturn(cookie);
    	
    	mockMvc.perform(delete("/auth")
    			.cookie(new Cookie("jwt_token", "valid-token")))
    			.andExpect(status().isNoContent())
    			.andExpect(header().string("Set-Cookie", "jwt_token=; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT"));
    }
}