package com.smartpaymentsystem.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class DashboardResponseDTO {
    private Long totalPayments;
    private Long pendingCount;
    private Long paidCount;
    private Long overdueCount;
    private Long canceledCount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
}
