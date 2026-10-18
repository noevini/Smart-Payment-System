package com.smartpaymentsystem.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
