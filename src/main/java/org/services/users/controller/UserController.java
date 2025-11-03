package org.services.users.controller;

import lombok.RequiredArgsConstructor;
import org.services.users.dto.request.CreateUserRequest;

import org.services.users.dto.response.UserResponse;
import org.services.users.model.UserEntity;
import org.services.users.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserEntity createUser(@RequestBody CreateUserRequest user) {
        return  userService.saveUser(user);
    }
    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

}
