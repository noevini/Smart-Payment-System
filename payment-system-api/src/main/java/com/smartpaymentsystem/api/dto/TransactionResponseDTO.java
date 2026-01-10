package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class TransactionResponseDTO {
    private Long id;
    private Long businessId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Instant occurredAt;
    private Instant createdAt;
    private Instant updatedAt;
}
