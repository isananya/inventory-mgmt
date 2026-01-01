package com.chubb.inventoryapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    @NotNull(message = "Product is required")
	private Long productId;
	
    @NotNull(message = "Warehouse is required")
	private Long warehouseId;

	@Min(value = 0, message = "Stock quantity cannot be negative")
	private Integer quantity;

	@Min(value = 0)
	private Integer lowStockThreshold = 10;
}
