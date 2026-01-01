package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class WarehouseAlreadyExistsException extends RuntimeException {
	public WarehouseAlreadyExistsException(String name, String location) {
		super("Warehouse " + name + " in "+location + " already exists");
	}
}
