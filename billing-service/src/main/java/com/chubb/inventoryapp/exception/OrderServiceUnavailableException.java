package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class OrderServiceUnavailableException extends RuntimeException {
	public OrderServiceUnavailableException(String msg) {
		super(msg);
	}
}
