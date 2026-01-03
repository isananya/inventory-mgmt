package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {
	public CategoryNotFoundException(Long categoryId) {
        super("Category id " + categoryId + " not found");
    }
}
