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
    void handleInvoiceAlreadyExists_ReturnsConflict() {
        InvoiceAlreadyExistsException ex = new InvoiceAlreadyExistsException("Invoice exists");
        ResponseEntity<String> response = handler.handleInvoiceAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Invoice exists", response.getBody());
    }

    @Test
    void handleInvoiceNotFound_ReturnsNotFound() {
        InvoiceNotFoundException ex = new InvoiceNotFoundException("Invoice missing");
        ResponseEntity<String> response = handler.handleInvoiceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Invoice missing", response.getBody());
    }

    @Test
    void handleOrderServiceUnavailable_ReturnsServiceUnavailable() {
        OrderServiceUnavailableException ex = new OrderServiceUnavailableException("Order Service down");
        ResponseEntity<String> response = handler.handleOrderServiceUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Order Service down", response.getBody());
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