package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.inventoryapp.dto.UserProfileResponse;
import com.chubb.inventoryapp.exception.UserNotFoundException;
import com.chubb.inventoryapp.model.Role;
import com.chubb.inventoryapp.model.User;
import com.chubb.inventoryapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("Ananya Nayak");
        user1.setEmail("user@gmail.com");
        user1.setRole(Role.CUSTOMER);
        user1.setActive(true);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Aashinya Nayak");
        user2.setEmail("admin@gmail.com");
        user2.setRole(Role.ADMIN);
        user2.setActive(true);
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserProfileResponse> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Ananya Nayak", result.get(0).getName());
        assertEquals("Aashinya Nayak", result.get(1).getName());
        assertEquals("ADMIN", result.get(1).getRole());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.deleteUser(1L);

        assertFalse(user1.isActive());
        verify(userRepository).save(user1);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).save(any(User.class));
    }
}