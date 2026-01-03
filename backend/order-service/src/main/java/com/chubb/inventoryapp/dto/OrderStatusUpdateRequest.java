package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    @NotNull(message = "Status cannot be empty")
    private OrderStatus status;
}
