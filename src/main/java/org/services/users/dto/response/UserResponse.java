package org.services.users.dto.response;



import java.time.LocalDate;


public record UserResponse(

        String firstName,
        String lastName,
        Long identityDocument,
        String phoneNumber,
        LocalDate birthDate,
        String email,
        String roleName
) {}