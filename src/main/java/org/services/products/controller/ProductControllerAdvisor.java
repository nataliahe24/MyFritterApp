package org.services.products.controller;

import lombok.extern.slf4j.Slf4j;
import org.services.configurations.exceptions.ExceptionResponse;
import org.services.configurations.exceptions.ExceptionMessages;
import org.services.products.utils.exceptions.ImageUploadException;
import org.services.products.utils.exceptions.InvalidImageFormatException;
import org.services.products.utils.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.time.LocalDateTime;

import static org.services.configurations.exceptions.ExceptionMessages.PRODUCT_NOT_FOUND_MESSAGE_ES;


@Slf4j
@ControllerAdvice
public class ProductControllerAdvisor {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleProductNotFoundException(
            ProductNotFoundException exception) {
        return new ResponseEntity<>(
                new ExceptionResponse(PRODUCT_NOT_FOUND_MESSAGE_ES,LocalDateTime.now()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ExceptionResponse> handleImageUploadException(
            ImageUploadException exception) {

        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(ExceptionMessages.INTERNAL_SERVER_ERROR_MESSAGE_ES,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidImageFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidImageFormatException(
            InvalidImageFormatException exception) {
        
        log.error("Invalid image format: {}", exception.getMessage());
        
        ExceptionResponse response = new ExceptionResponse(
                ExceptionMessages.INVALID_IMAGE_FORMAT_MESSAGE_ES,
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


} 