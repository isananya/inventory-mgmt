package com.chubb.inventoryapp.dto;

import lombok.Data;

@Data
public class StockCheckResponse {
	private Long productId;
    private boolean available;
    private Long warehouseId;
    private float price;
}

