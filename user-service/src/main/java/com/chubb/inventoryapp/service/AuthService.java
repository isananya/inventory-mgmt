package com.chubb.inventoryapp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.ChangePasswordRequest;
import com.chubb.inventoryapp.dto.LoginRequest;
import com.chubb.inventoryapp.dto.LoginResponse;
import com.chubb.inventoryapp.dto.SignupRequest;
import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.exception.PasswordMismatchException;
import com.chubb.inventoryapp.exception.UserAlreadyExistsException;
import com.chubb.inventoryapp.exception.UserNotFoundException;
import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;
import com.chubb.inventoryapp.repository.UserRepository;
import com.chubb.inventoryapp.security.JwtService;
import com.chubb.inventoryapp.security.UserDetailsImpl;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public User register(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			User user1 = userRepository.findByEmail(request.getEmail()).get();
			if(user1.isActive()==true) {
				throw new UserAlreadyExistsException();
			}
			user1.setActive(true);
			user1.setPassword(passwordEncoder.encode(request.getPassword()));
			user1.setName(request.getName());
			Role role1 = (request.getRole() == null) ? Role.CUSTOMER : request.getRole();
			user1.setRole(role1);
			return userRepository.save(user1);
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		Role role = (request.getRole() == null) ? Role.CUSTOMER : request.getRole();
		user.setRole(role);

		return userRepository.save(user);
	}
	
	public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        String jwt = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("jwt_token", jwt)
                .httpOnly(true)
                .secure(false) 
                .path("/")
                .maxAge(2 * 60 * 60) // 2 hours
                .build();

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        String role = user.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("CUSTOMER");
        body.put("role", role);
        body.put("name", user.getUser().getName());
        
        return new LoginResponse(cookie, body);
	}
	
	public ResponseCookie logout() {
		return ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
	}
	
	public UserProfileResponse getProfile(String token) {
        String email = jwtService.extractUsername(token);
        
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		
		return new UserProfileResponse(
	            user.getId(),
	            user.getName(),
	            user.getEmail(),
	            user.getRole().name(),
	            user.isActive()
	    );		
	}
	
	public void changePassword(String token, ChangePasswordRequest request) {

        String email = jwtService.extractUsername(token);

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new PasswordMismatchException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}
	
	public void deleteAccount(String token) {
		String email = jwtService.extractUsername(token);
		User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("User not found"));

	    user.setActive(false);
	    userRepository.save(user);
	}
}
