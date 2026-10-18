package com.smartpaymentsystem.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {

    @Size(max = 30)
    private String name;

    @Size(max = 20)
    private String phone;

    @AssertTrue(message = "At least one field (name or phone) must be provided")
    public boolean isAtLeastOneFieldProvided() {
        return (name != null && !name.trim().isEmpty())
                || (phone != null && !phone.trim().isEmpty());
    }
}
