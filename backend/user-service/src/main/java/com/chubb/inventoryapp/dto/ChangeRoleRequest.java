package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    @NotNull(message = "Role is required")
    private Role role;
}
