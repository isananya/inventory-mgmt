package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InventoryNotFoundException extends RuntimeException {
	public InventoryNotFoundException(String msg) {
        super(msg);
    }
}
