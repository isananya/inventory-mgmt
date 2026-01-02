package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    @NotBlank(message = "Status cannot be empty")
    private OrderStatus status;
}
