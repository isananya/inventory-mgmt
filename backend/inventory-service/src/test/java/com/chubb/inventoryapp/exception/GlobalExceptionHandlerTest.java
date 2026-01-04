package com.chubb.inventoryapp.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleCategoryAlreadyExists_ReturnsConflict() {
        CategoryAlreadyExistsException ex = mock(CategoryAlreadyExistsException.class);
        when(ex.getMessage()).thenReturn("Category exists");

        ResponseEntity<String> response = handler.handleCategoryAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Category exists", response.getBody());
    }

    @Test
    void handleCategoryNotFound_ReturnsNotFound() {
        CategoryNotFoundException ex = mock(CategoryNotFoundException.class);
        when(ex.getMessage()).thenReturn("Category not found");

        ResponseEntity<String> response = handler.handleCategoryNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void handleProductAlreadyExists_ReturnsConflict() {
        ProductAlreadyExistsException ex = mock(ProductAlreadyExistsException.class);
        when(ex.getMessage()).thenReturn("Product exists");

        ResponseEntity<String> response = handler.handleProductAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Product exists", response.getBody());
    }

    @Test
    void handleProductNotFound_ReturnsNotFound() {
        ProductNotFoundException ex = mock(ProductNotFoundException.class);
        when(ex.getMessage()).thenReturn("Product not found");

        ResponseEntity<String> response = handler.handleProductNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void handleWarehouseAlreadyExists_ReturnsConflict() {
        WarehouseAlreadyExistsException ex = mock(WarehouseAlreadyExistsException.class);
        when(ex.getMessage()).thenReturn("Warehouse exists");

        ResponseEntity<String> response = handler.handleWarehouseAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Warehouse exists", response.getBody());
    }

    @Test
    void handleWarehouseNotFound_ReturnsNotFound() {
        WarehouseNotFoundException ex = mock(WarehouseNotFoundException.class);
        when(ex.getMessage()).thenReturn("Warehouse not found");

        ResponseEntity<String> response = handler.handleWarehouseNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Warehouse not found", response.getBody());
    }

    @Test
    void handleInventoryAlreadyExists_ReturnsConflict() {
        InventoryAlreadyExistsException ex = mock(InventoryAlreadyExistsException.class);
        when(ex.getMessage()).thenReturn("Inventory exists");

        ResponseEntity<String> response = handler.handleInventoryAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Inventory exists", response.getBody());
    }

    @Test
    void handleInventoryNotFound_ReturnsNotFound() {
        InventoryNotFoundException ex = mock(InventoryNotFoundException.class);
        when(ex.getMessage()).thenReturn("Inventory not found");

        ResponseEntity<String> response = handler.handleInventoryNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Inventory not found", response.getBody());
    }

    @Test
    void handleInsufficientStock_ReturnsBadRequest() {
        InsufficientStockException ex = mock(InsufficientStockException.class);
        when(ex.getMessage()).thenReturn("Not enough stock");

        ResponseEntity<String> response = handler.handleInsufficientStock(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not enough stock", response.getBody());
    }

    @Test
    void handleValidationErrors_ReturnsBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        // FieldError(objectName, field, defaultMessage)
        FieldError fieldError = new FieldError("productRequest", "name", "cannot be empty");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<String> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("name: cannot be empty", response.getBody());
    }

    @Test
    void handleValidationErrors_NoErrors_ReturnsDefaultMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<String> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody());
    }
}