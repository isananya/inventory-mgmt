package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductAlreadyExistsException extends RuntimeException{
	public ProductAlreadyExistsException(String brand, String name) {
		super(brand + " " + name + " already exists");
	}

}
