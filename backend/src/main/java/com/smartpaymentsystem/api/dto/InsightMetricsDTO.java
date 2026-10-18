package com.smartpaymentsystem.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InsightMetricsDTO {
    private Long businessId;
    private Long totalPayments;
    private Long paidPayments;
    private Long pendingPayments;
    private Long overduePayments;
    private BigDecimal totalRevenue;
    private BigDecimal totalPendingAmount;
    private BigDecimal totalOverdueAmount;
}
