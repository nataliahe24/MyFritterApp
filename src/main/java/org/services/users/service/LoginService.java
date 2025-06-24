package org.services.users.service;

import lombok.RequiredArgsConstructor;
import org.services.users.dto.request.LoginRequest;
import org.services.users.dto.response.LoginResponse;
import org.services.users.model.UserEntity;
import org.services.users.repository.UserRepository;
import org.services.users.utils.config.PasswordEncoderAdapter;
import org.services.users.utils.exceptions.InvalidPasswordException;
import org.services.users.utils.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;

    public LoginResponse login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!passwordEncoderAdapter.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Contraseña incorrecta");
        }

        return new LoginResponse(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                "Login exitoso",
                user.getRole().toString()
        );
    }
}


