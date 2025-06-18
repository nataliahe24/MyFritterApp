package org.services.users.service;

import lombok.RequiredArgsConstructor;
import org.services.users.dto.request.LoginRequest;
import org.services.users.model.UserEntity;
import org.services.users.repository.UserRepository;
import org.services.users.utils.config.PasswordEncoderAdapter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;

    public boolean login(LoginRequest loginRequest) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            return passwordEncoderAdapter.matches(loginRequest.getPassword(), user.getPassword());
        }
        return false;
    }
}
