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
    void handleInventoryServiceUnavailable_ReturnsServiceUnavailable() {
        InventoryServiceUnavailableException ex = new InventoryServiceUnavailableException("Service down");
        ResponseEntity<String> response = handler.handleInventoryServiceUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service down", response.getBody());
    }

    @Test
    void handleOrderNotFound_ReturnsNotFound() {
        OrderNotFoundException ex = new OrderNotFoundException("Order missing");
        ResponseEntity<String> response = handler.handleOrderNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order missing", response.getBody());
    }

    @Test
    void handleOutOfStock_ReturnsBadRequest() {
        OutOfStockException ex = new OutOfStockException("No stock");
        ResponseEntity<String> response = handler.handleOutOfStock(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No stock", response.getBody());
    }

    @Test
    void handleStatusConflict_ReturnsConflict() {
        StatusConflictException ex = new StatusConflictException("Conflict status");
        ResponseEntity<String> response = handler.handleStatusConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict status", response.getBody());
    }

    @Test
    void handleValidationErrors_ReturnsBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "defaultMessage");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<String> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("field: defaultMessage", response.getBody());
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