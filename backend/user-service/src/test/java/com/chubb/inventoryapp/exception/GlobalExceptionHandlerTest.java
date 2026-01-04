package com.chubb.inventoryapp.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidCredentials_ReturnsUnauthorized() {
        InvalidCredentialsException ex = mock(InvalidCredentialsException.class);
        when(ex.getMessage()).thenReturn("Bad credentials");

        ResponseEntity<String> response = handler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody());
    }

    @Test
    void handlePasswordMismatch_ReturnsBadRequest() {
        PasswordMismatchException ex = mock(PasswordMismatchException.class);
        when(ex.getMessage()).thenReturn("Passwords do not match");

        ResponseEntity<String> response = handler.handlePasswordMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Passwords do not match", response.getBody());
    }

    @Test
    void handleUserAlreadyExists_ReturnsConflict() {
        UserAlreadyExistsException ex = mock(UserAlreadyExistsException.class);
        when(ex.getMessage()).thenReturn("User already exists");

        ResponseEntity<String> response = handler.handleUserAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    void handleUserNotFound_ReturnsNotFound() {
        UserNotFoundException ex = mock(UserNotFoundException.class);
        when(ex.getMessage()).thenReturn("User not found");

        ResponseEntity<String> response = handler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void handleInactive_ReturnsForbidden() {
        UserInactiveException ex = mock(UserInactiveException.class);
        when(ex.getMessage()).thenReturn("User is inactive");

        ResponseEntity<String> response = handler.handleInactive(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User is inactive", response.getBody());
    }

    @Test
    void handleValidationErrors_ReturnsBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("userDto", "email", "must be a valid email");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("email"));
        assertEquals("must be a valid email", response.getBody().get("email"));
    }
}