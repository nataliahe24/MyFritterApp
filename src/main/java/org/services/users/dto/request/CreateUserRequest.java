package org.services.users.dto.request;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
@Getter
public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private Long identityDocument;
    private String phoneNumber;
    private LocalDate birthDate;
    private String email;
    private String password;
    private String role;

}
