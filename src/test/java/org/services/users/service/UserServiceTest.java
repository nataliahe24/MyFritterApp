package org.services.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.services.users.dto.request.CreateUserRequest;
import org.services.users.dto.response.UserResponse;
import org.services.users.model.RoleEntity;
import org.services.users.model.UserEntity;
import org.services.users.repository.RoleRepository;
import org.services.users.repository.UserRepository;
import org.services.users.utils.config.PasswordEncoderAdapter;
import org.services.users.utils.exceptions.UserAlreadyExistsException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderAdapter passwordEncoderAdapter;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest validRequest;
    private UserEntity existingUser;
    private RoleEntity userRole;
    private UserEntity savedUser;

    @BeforeEach
    void setUp() {
        validRequest = new CreateUserRequest();
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setIdentityDocument(12345678L);
        validRequest.setPhoneNumber("+1234567890");
        validRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        validRequest.setEmail("john.doe@example.com");
        validRequest.setPassword("password123");
        validRequest.setRole("USER");

        existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setEmail("john.doe@example.com");

        userRole = new RoleEntity();
        userRole.setId(1L);
        userRole.setName("USER");

        savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setRole(userRole);
    }

    @Test
    void saveUser_Success() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(validRequest.getRole())).thenReturn(Optional.of(userRole));
        when(passwordEncoderAdapter.encode(validRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Act
        UserEntity result = userService.saveUser(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getFirstName(), result.getFirstName());
        assertEquals(savedUser.getEmail(), result.getEmail());

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(roleRepository).findByName(validRequest.getRole());
        verify(passwordEncoderAdapter).encode(validRequest.getPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void saveUser_UserAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.saveUser(validRequest);
        });

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void saveUser_InvalidEmail_ThrowsException() {
        // Arrange
        validRequest.setEmail("invalid-email");
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(validRequest);
        });

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void saveUser_RoleNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(validRequest.getRole())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(validRequest);
        });

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(roleRepository).findByName(validRequest.getRole());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void saveUser_PasswordEncoded() {
        // Arrange
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName(validRequest.getRole())).thenReturn(Optional.of(userRole));
        when(passwordEncoderAdapter.encode(validRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Act
        userService.saveUser(validRequest);

        // Assert
        verify(passwordEncoderAdapter).encode(validRequest.getPassword());
        verify(userRepository).save(argThat(user -> 
            "encodedPassword".equals(user.getPassword())
        ));
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<UserEntity> users = Arrays.asList(savedUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(savedUser.getFirstName(), result.get(0).firstName());
        assertEquals(savedUser.getLastName(), result.get(0).lastName());
        assertEquals(savedUser.getEmail(), result.get(0).email());
        assertEquals(savedUser.getRole().getName(), result.get(0).roleName());

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void saveUser_ValidEmailFormats() {
        // Arrange
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@example.com"
        };

        when(roleRepository.findByName(validRequest.getRole())).thenReturn(Optional.of(userRole));
        when(passwordEncoderAdapter.encode(validRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        for (String email : validEmails) {
            // Arrange for each email
            validRequest.setEmail(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act
            UserEntity result = userService.saveUser(validRequest);

            // Assert
            assertNotNull(result);
            verify(userRepository).findByEmail(email);
        }
    }

    @Test
    void saveUser_InvalidEmailFormats() {
        // Arrange
        String[] invalidEmails = {
            "invalid-email",
            "@example.com",
            "user@",
            "user.example.com",
            ""
        };

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        for (String email : invalidEmails) {
            // Arrange for each email
            validRequest.setEmail(email);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                userService.saveUser(validRequest);
            });
        }
    }
} 