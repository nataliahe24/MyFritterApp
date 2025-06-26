package org.services.configurations.exceptions;

import java.time.LocalDateTime;

public record ExceptionResponse(String message, LocalDateTime timeStamp) {
}
