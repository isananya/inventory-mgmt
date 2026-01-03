package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class InventoryServiceUnavailableException extends RuntimeException {
	public InventoryServiceUnavailableException(String msg) {
		super(msg);
	}
}
