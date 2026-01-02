package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.FulfillmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
	private Long id;
    private Long productId;
    private Integer quantity;
    private Long warehouseId;
    private float price;
    private FulfillmentStatus fulfillmentStatus;
}

