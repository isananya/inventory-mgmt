package com.chubb.inventoryapp.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String msg) {
        super(msg);
    }
}
