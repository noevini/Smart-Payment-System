package com.smartpaymentsystem.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionSummaryDTO {

    private Long businessId;

    private Long pendingReceivablePayments;
    private Long highRiskPayments;
    private Long mediumRiskPayments;
    private Long lowRiskPayments;

    private String overallRiskLevel;
    private String predictionSummary;
}