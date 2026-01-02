package com.chubb.inventoryapp.dto;

import lombok.Data;

@Data
public class StockUpdateRequest {
    private Long productId;
    private Long warehouseId;
    private int quantity;
}

