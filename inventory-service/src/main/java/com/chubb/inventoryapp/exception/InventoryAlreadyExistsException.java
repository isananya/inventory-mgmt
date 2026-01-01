package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InventoryAlreadyExistsException extends RuntimeException {
	public InventoryAlreadyExistsException(Long productId, Long warehouseId) {
		super("Inventory Already Exists");
	}
	
}
