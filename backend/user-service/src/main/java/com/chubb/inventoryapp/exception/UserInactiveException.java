package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
}

