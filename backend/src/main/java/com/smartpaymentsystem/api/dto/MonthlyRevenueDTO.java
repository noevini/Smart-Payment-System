package com.smartpaymentsystem.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Data
@Builder
public class MonthlyRevenueDTO {
    private Instant monthStart;
    private BigDecimal revenue;
    private long count;
}
