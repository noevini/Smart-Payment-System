package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import org.springframework.stereotype.Component;

@Component
public class InsightPromptBuilder {

    public String buildSummaryPrompt(InsightMetricsDTO metrics) {

        return """
            You are a financial assistant for a small business payment system.

            Analyze the following business metrics and provide insights.

            Business Metrics:
            - Total Payments: %d
            - Paid Payments: %d
            - Pending Payments: %d
            - Overdue Payments: %d
            - Total Revenue: %s
            - Pending Amount: %s
            - Overdue Amount: %s

            Instructions:
            - Provide a short summary of the business performance
            - Identify key risks
            - Suggest actionable recommendations
            - Provide a confidence level (low, medium, high)

            Respond in JSON format with:
            {
              "summary": "...",
              "risks": ["...", "..."],
              "recommendations": ["...", "..."],
              "confidence": "..."
            }
            """.formatted(
                metrics.getTotalPayments(),
                metrics.getPaidPayments(),
                metrics.getPendingPayments(),
                metrics.getOverduePayments(),
                metrics.getTotalRevenue(),
                metrics.getTotalPendingAmount(),
                metrics.getTotalOverdueAmount()
        );
    }
}
