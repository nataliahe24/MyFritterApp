package org.services.orders.utils.exceptions;

public class ErrorCreatingTrackingCodeException extends RuntimeException {
    public ErrorCreatingTrackingCodeException(String message) {
        super(message);
    }
}
