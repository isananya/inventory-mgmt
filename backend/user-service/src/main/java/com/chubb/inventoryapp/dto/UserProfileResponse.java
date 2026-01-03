package com.chubb.inventoryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean active;
}

