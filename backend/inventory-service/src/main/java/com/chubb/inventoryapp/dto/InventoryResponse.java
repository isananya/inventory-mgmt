package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
	private Long id;
    private Product product;
    private Warehouse warehouse;
    private Integer quantity;
    private Integer lowStockThreshold = 10;
}
