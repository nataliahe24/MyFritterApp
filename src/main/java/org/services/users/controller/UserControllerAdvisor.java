package org.services.users.controller;

import org.services.configurations.exceptions.ExceptionResponse;
import org.services.users.utils.exceptions.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@RestControllerAdvice
public class UserControllerAdvisor {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserAlreadyExistsException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(exception.getMessage(), LocalDateTime.now()));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Solicitud inválida");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(message, LocalDateTime.now()));
    }
}
