package com.chubb.inventoryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CategoryAlreadyExistsException.class)
	public ResponseEntity<String> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<String> handleCategoryNotFound(CategoryNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(ProductAlreadyExistsException.class)
	public ResponseEntity<String> handleProductAlreadyExists(ProductAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(WarehouseAlreadyExistsException.class)
	public ResponseEntity<String> handleWarehouseAlreadyExists(WarehouseAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(WarehouseNotFoundException.class)
	public ResponseEntity<String> handleWarehouseNotFound(WarehouseNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(InventoryAlreadyExistsException.class)
	public ResponseEntity<String> handleInventoryAlreadyExists(InventoryAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(InventoryNotFoundException.class)
	public ResponseEntity<String> handleInventoryNotFound(InventoryNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<String> handleInsufficientStock(InsufficientStockException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.findFirst()
				.orElse("Validation failed");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}
}
