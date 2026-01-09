package com.smartpaymentsystem.api.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class UpdatePaymentRequest {

    @DecimalMin("0.01")
    private BigDecimal amount;

    private String currency;
    private String description;
    private Instant dueDate;

}
