package org.services.products.utils.exceptions;

public class ImageUploadException extends RuntimeException {
    
    public ImageUploadException(String message) {
        super(message);
    }
    
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
} 