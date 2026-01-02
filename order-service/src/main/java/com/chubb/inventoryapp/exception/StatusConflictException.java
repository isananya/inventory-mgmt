package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StatusConflictException extends RuntimeException {
	public StatusConflictException(String msg) {
		super(msg);
	}
}
