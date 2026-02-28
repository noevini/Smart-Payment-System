package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class UpdatePaymentRequestDTO {

    @DecimalMin("0.01")
    private BigDecimal amount;

    private String currency;
    private String description;
    private Instant dueDate;
    private PaymentStatus status;
}
