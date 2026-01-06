package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.CreateUserRequest;
import com.smartpaymentsystem.api.dto.UserResponse;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User userCreated = userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getRole());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userCreated.getId());
        userResponse.setName(userCreated.getName());
        userResponse.setEmail(userCreated.getEmail());
        userResponse.setPhone(userCreated.getPhone());
        userResponse.setRole(userCreated.getRole());
        userResponse.setCreatedAt(userCreated.getCreatedAt());
        userResponse.setUpdatedAt(userCreated.getUpdatedAt());

        return userResponse;
    }
}
