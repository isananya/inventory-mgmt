package com.chubb.inventoryapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.ChangePasswordRequest;
import com.chubb.inventoryapp.dto.LoginRequest;
import com.chubb.inventoryapp.dto.LoginResponse;
import com.chubb.inventoryapp.dto.SignupRequest;
import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.model.User;
import com.chubb.inventoryapp.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest dto) {
        User user = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getId());
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    	LoginResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getCookie().toString())
                .body(response.getBody());
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = authService.logout();
        
        return ResponseEntity.noContent()
        		.header(HttpHeaders.SET_COOKIE, cookie.toString())
        		.build();
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@CookieValue("jwt_token") String token){
    	UserProfileResponse response = authService.getProfile(token);
    	return ResponseEntity.ok(response);
    }
    
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@CookieValue("jwt_token") String token,
            @RequestBody @Valid ChangePasswordRequest request) {

        authService.changePassword(token, request);

        return ResponseEntity.noContent().build();
    }
}
