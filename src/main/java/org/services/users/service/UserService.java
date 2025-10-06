package org.services.users.service;

import lombok.RequiredArgsConstructor;
import org.services.users.dto.request.CreateUserRequest;
import org.services.users.dto.response.UserResponse;
import org.services.users.model.UserEntity;
import org.services.users.model.RoleEntity;
import org.services.users.repository.RoleRepository;
import org.services.users.repository.UserRepository;
import org.services.users.utils.config.PasswordEncoderAdapter;
import org.services.users.utils.exceptions.UserAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;
    private final RoleRepository roleRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public UserEntity saveUser(CreateUserRequest request) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("El correo ya está registrado: " + request.getEmail());
        }

        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        RoleEntity role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + request.getRole()));

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIdentityDocument(request.getIdentityDocument());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(role);

        String encodedPassword = passwordEncoderAdapter.encode(request.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getIdentityDocument(),
                        user.getPhoneNumber(),
                        user.getBirthDate(),
                        user.getEmail(),
                        user.getRole().getName()
                ))
                .collect(Collectors.toList());
    }
    
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}