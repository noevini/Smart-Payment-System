package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private Long userId;
    private UserRole role;
    private Long businessId;
}
