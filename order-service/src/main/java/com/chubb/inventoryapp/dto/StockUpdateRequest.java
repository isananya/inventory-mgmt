package com.chubb.inventoryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockUpdateRequest {
    private Long productId;
    private Long warehouseId;
    private int quantity;
}

