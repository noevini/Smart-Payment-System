package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotNull(message = "Business id is required")
    private Long businessId;

    @NotNull
    private TransactionType type;

    @DecimalMin("0.01")
    @NotNull
    private BigDecimal amount;

    @Size(min = 3, max = 3)
    @NotBlank
    private String currency;

    @NotBlank
    private String description;

    @NotNull
    private Instant occurredAt;
}
