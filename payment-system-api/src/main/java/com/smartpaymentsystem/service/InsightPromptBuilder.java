package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import org.springframework.stereotype.Component;

@Component
public class InsightPromptBuilder {

    public String buildSummaryPrompt(InsightMetricsDTO metrics) {
        return """
                You are an AI financial assistant for a Smart Payment System used by small businesses.

                Your task is to analyze the business payment metrics and return a concise business insight.

                Business ID: %d

                Metrics:
                - Total receivable payments: %d
                - Paid receivable payments: %d
                - Pending receivable payments: %d
                - Overdue receivable payments: %d
                - Total revenue collected: %s
                - Total pending receivable amount: %s
                - Total overdue receivable amount: %s

                Instructions:
                - Write a short and clear summary of the business payment performance.
                - Identify up to 3 main risks.
                - Suggest up to 3 practical recommendations.
                - Set confidence as one of: low, medium, high.
                - Focus only on the metrics provided.
                - Do not invent information that is not present in the metrics.
                - Keep the tone professional and concise.

                Return ONLY valid JSON in this exact structure:
                {
                  "summary": "string",
                  "risks": ["string", "string"],
                  "recommendations": ["string", "string"],
                  "confidence": "low"
                }
                """.formatted(
                metrics.getBusinessId(),
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