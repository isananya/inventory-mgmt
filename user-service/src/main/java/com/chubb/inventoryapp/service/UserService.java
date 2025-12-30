package com.chubb.inventoryapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<UserProfileResponse> getAllUsers() {

        return userRepository.findAll()
        		.stream()
                .map(user -> new UserProfileResponse(
                		user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.isActive()
                ))
                .toList();
    }
}
