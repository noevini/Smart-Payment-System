package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private Long businessId;
}
