package com.smartpaymentsystem.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBusinessRequestDTO {

    @Size(min = 10, max = 140)
    @NotBlank
    private String name;
}
