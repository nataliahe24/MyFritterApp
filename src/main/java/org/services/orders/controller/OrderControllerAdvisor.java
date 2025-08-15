package org.services.orders.controller;

import lombok.extern.slf4j.Slf4j;
import org.services.configurations.exceptions.ExceptionResponse;
import org.services.orders.exceptions.InsufficientStockException;
import org.services.orders.exceptions.OrderException;
import org.services.orders.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class OrderControllerAdvisor {

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ExceptionResponse> handleOrderException(OrderException exception) {
        log.error("Order error: {}", exception.getMessage());
        
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(
                        exception.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleProductNotFoundException(ProductNotFoundException exception) {
        log.error("Product not found in order: {}", exception.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ExceptionResponse> handleInsufficientStockException(InsufficientStockException exception) {
        log.error("Insufficient stock: {}", exception.getMessage());
        
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(
                        exception.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Invalid argument: {}", exception.getMessage());
        
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(
                        "Parámetros inválidos",
                        LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception exception) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        "Error interno del servidor",
                        LocalDateTime.now()));
    }
} 