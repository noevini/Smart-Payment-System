package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.UserRole;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private Instant createdAt;
    private Instant updatedAt;
}
