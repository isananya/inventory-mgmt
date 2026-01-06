package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.ChangeRoleRequest;
import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping("")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<Void> changeUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeRoleRequest request) {

        userService.changeUserRole(userId, request.getRole());
        return ResponseEntity.ok().build();
    }
	
}
