package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.PaymentDirection;
import com.smartpaymentsystem.domain.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PaymentResponseDTO {

    private Long id;
    private Long businessId;
    private PaymentDirection direction;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Instant dueDate;
    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;
}
