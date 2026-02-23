package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class OverduePaymentDTO {
    private Long paymentId;
    private BigDecimal amount;
    private Instant dueDate;
    private PaymentStatus status;
}
