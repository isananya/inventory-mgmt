package com.chubb.inventoryapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.exception.UserInactiveException;
import com.chubb.inventoryapp.exception.UserNotFoundException;
import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;
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
	
	public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setActive(false);
        userRepository.save(user);
    }

    public void changeUserRole(Long userId, Role role) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UserInactiveException("User account is inactive");
        }

        user.setRole(role);
        userRepository.save(user);
    }
}
