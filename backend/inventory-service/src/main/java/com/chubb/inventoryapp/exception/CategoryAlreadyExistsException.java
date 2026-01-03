package com.chubb.inventoryapp.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String categoryName) {
        super("Category with name '" + categoryName + "' already exists");
    }
}
