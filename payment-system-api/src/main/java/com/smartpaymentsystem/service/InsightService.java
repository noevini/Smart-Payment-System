package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import com.smartpaymentsystem.api.dto.InsightResponseDTO;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentDirection;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class InsightService {

    private final BusinessAccessService businessAccessService;
    private final PaymentRepository paymentRepository;
    private final InsightPromptBuilder insightPromptBuilder;
    private final ChatClient.Builder chatClientBuilder;

    public InsightResponseDTO generateSummary(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        InsightMetricsDTO metrics = buildMetrics(businessId);

        String prompt = insightPromptBuilder.buildSummaryPrompt(metrics);

        ChatClient chatClient = chatClientBuilder.build();

        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        InsightResponseDTO response = new InsightResponseDTO();
        response.setSummary(aiResponse);
        response.setRisks(List.of("Temporary risk"));
        response.setRecommendations(List.of("Temporary recommendation"));
        response.setConfidence("low");

        return response;
    }

    private InsightMetricsDTO buildMetrics(Long businessId) {
        List<Payment> payments = paymentRepository.findByBusiness_Id(businessId);

        InsightMetricsDTO metrics = new InsightMetricsDTO();

        long totalPayments = 0;
        long paidPayments = 0;
        long pendingPayments = 0;
        long overduePayments = 0;

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalPendingAmount = BigDecimal.ZERO;
        BigDecimal totalOverdueAmount = BigDecimal.ZERO;

        for (Payment payment : payments) {

            if (payment.getDirection() != PaymentDirection.RECEIVABLE) {
                continue;
            }

            totalPayments++;

            switch (payment.getStatus()) {
                case PAID:
                    paidPayments++;
                    totalRevenue = totalRevenue.add(payment.getAmount());
                    break;

                case PENDING:
                    pendingPayments++;
                    totalPendingAmount = totalPendingAmount.add(payment.getAmount());
                    break;

                case OVERDUE:
                    overduePayments++;
                    totalOverdueAmount = totalOverdueAmount.add(payment.getAmount());
                    break;

                default:
                    break;
            }
        }

        metrics.setBusinessId(businessId);
        metrics.setTotalPayments(totalPayments);
        metrics.setPaidPayments(paidPayments);
        metrics.setPendingPayments(pendingPayments);
        metrics.setOverduePayments(overduePayments);
        metrics.setTotalRevenue(totalRevenue);
        metrics.setTotalPendingAmount(totalPendingAmount);
        metrics.setTotalOverdueAmount(totalOverdueAmount);

        return metrics;
    }
}