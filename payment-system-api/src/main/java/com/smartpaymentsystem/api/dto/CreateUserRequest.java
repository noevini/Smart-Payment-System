package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @Size(max = 120)
    @NotBlank
    private String name;

    @Size(min = 15, max = 120)
    @Email
    @NotBlank
    private String email;

    @Size(max = 30)
    private String phone;

    @NotBlank
    private String password;

    @NotNull
    private UserRole role;
}
