package com.chubb.inventoryapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.chubb.inventoryapp.model.Address;
import com.chubb.inventoryapp.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long customerId;
    private OrderStatus status;
    private Address address;
    private float totalAmount;
    private LocalDateTime createdAt;
}

