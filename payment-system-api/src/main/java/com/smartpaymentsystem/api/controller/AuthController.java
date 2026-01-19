package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.LoginRequestDTO;
import com.smartpaymentsystem.api.dto.LoginResponseDTO;
import com.smartpaymentsystem.api.dto.RegisterRequestDTO;
import com.smartpaymentsystem.api.dto.RegisterResponseDTO;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        User user = authService.register(request);

        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setBusinessId(user.getBusiness() != null ? user.getBusiness().getId() : null);

        return response;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

}
