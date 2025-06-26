package org.services.products.dto.response;

import java.time.LocalDateTime;

public record  SaveProductResponse(
        String message,
        LocalDateTime time
) {
}