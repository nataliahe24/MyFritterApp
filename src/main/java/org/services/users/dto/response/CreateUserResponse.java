package org.services.users.dto.response;

import java.time.LocalDateTime;

public record CreateUserResponse(String message, LocalDateTime time) {
}
