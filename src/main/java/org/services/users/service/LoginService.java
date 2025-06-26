package org.services.users.service;

import lombok.RequiredArgsConstructor;
import org.services.configurations.exceptions.ExceptionMessages;
import org.services.users.dto.request.LoginRequest;
import org.services.users.dto.response.LoginResponse;
import org.services.users.model.UserEntity;
import org.services.users.repository.UserRepository;
import org.services.users.utils.config.PasswordEncoderAdapter;
import org.services.users.utils.exceptions.InvalidPasswordException;
import org.services.users.utils.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;

    public LoginResponse login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND_MESSAGE_ES));

        if (!passwordEncoderAdapter.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException(ExceptionMessages.INVALID_PASSWORD_MESSAGE_ES);
        }

        return new LoginResponse(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                ExceptionMessages.LOGIN_SUCCESS_MESSAGE_ES,
                user.getRole().toString()
        );
    }
}


