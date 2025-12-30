package com.chubb.inventoryapp.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.SignupRequest;
import com.chubb.inventoryapp.exception.UserAlreadyExistsException;
import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;
import com.chubb.inventoryapp.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;

	public AuthService(UserRepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}

	public User register(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));

		Role role = (request.getRole() == null) ? Role.CUSTOMER : request.getRole();
		user.setRole(role);

		return userRepository.save(user);
	}
}
