package com.chubb.inventoryapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long warehouseId;

    @Min(1)
    private Integer quantity;
}

