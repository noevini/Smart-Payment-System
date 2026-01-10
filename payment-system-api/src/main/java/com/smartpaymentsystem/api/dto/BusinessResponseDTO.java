package com.smartpaymentsystem.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BusinessResponseDTO {

    private Long id;
    private String name;
    private Long ownerId;
    private Instant createdAt;
    private Instant updatedAt;

}
