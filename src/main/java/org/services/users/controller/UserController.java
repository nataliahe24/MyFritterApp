package org.services.users.controller;


import lombok.RequiredArgsConstructor;
import org.services.users.dto.request.CreateUserRequest;

import org.services.users.model.UserEntity;
import org.services.users.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserEntity createUser(@RequestBody CreateUserRequest user) {
        return  userService.saveUser(user);
    }

}
