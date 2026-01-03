package com.chubb.inventoryapp.dto;

import java.util.Map;

import org.springframework.http.ResponseCookie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private ResponseCookie cookie;
    private Map<String, Object> body;
}

