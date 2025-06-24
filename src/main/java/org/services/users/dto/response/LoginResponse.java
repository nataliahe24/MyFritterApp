package org.services.users.dto.response;



public record LoginResponse(Long id,String name, String email, String message, String role) {
}
