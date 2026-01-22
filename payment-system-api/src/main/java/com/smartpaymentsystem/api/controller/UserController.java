package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.UpdateUserRequestDTO;
import com.smartpaymentsystem.api.dto.UserResponseDTO;
import com.smartpaymentsystem.api.mapper.UserMapper;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.security.CurrentUserService;
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
    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public UserResponseDTO getMe() {
        Long userId = currentUserService.getCurrentUserId();
        return UserMapper.toResponse(userService.getById(userId));
    }

    @PutMapping("/me")
    public UserResponseDTO updateUser(@Valid @RequestBody UpdateUserRequestDTO request) {
        Long userId = currentUserService.getCurrentUserId();
        User user = userService.updateUser(userId, request.getName(), request.getPhone());

        return UserMapper.toResponse(user);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser() {
        Long userId = currentUserService.getCurrentUserId();
        userService.deleteUser(userId);
    }
}
