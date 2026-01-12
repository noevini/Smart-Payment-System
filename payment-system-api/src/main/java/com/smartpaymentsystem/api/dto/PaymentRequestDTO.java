package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.PaymentDirection;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PaymentRequestDTO {

    @NotNull
    private PaymentDirection direction;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String currency;
    private String description;

    @NotNull
    private Instant dueDate;
}
