package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.CreateUserRequest;
import com.smartpaymentsystem.api.dto.UserResponse;
import com.smartpaymentsystem.api.mapper.UserMapper;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return UserMapper.toResponse(userService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User userCreated = userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getRole());

        return UserMapper.toResponse(userCreated);
    }
}
