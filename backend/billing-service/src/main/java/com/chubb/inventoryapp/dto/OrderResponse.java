package com.chubb.inventoryapp.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderResponse {
	private Long orderId;
    private Long customerId;
    private String status;
    private Address address;
    private float totalAmount;
    private LocalDateTime createdAt;
}
