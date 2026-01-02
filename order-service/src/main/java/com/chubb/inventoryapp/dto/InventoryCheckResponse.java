package com.chubb.inventoryapp.dto;

import lombok.Data;

@Data
public class InventoryCheckResponse {
    private boolean available;
    private Long warehouseId;
}

