package org.services.users.service;

import lombok.RequiredArgsConstructor;
import org.services.users.dto.request.CreateUserRequest;

import org.services.users.model.UserEntity;
import org.services.users.repository.UserRepository;
import org.services.users.utils.config.PasswordEncoderAdapter;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public UserEntity saveUser(CreateUserRequest request) {

        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException();
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIdentityDocument(request.getIdentityDocument());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        String encodedPassword = passwordEncoderAdapter.encode(request.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}