package com.chubb.inventoryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckResponse {
	private Long productId;
    private boolean available;
    private Long warehouseId;
    private float price;
}
