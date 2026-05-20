package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.PredictionSummaryDTO;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentStatus;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class PredictionService {

    private final BusinessAccessService businessAccessService;
    private final PaymentRepository paymentRepository;

    public PredictionSummaryDTO getSummary(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        List<Payment> payments = paymentRepository.findByBusiness_Id(businessId);

        long pendingReceivable = 0;
        long highRisk = 0;
        long mediumRisk = 0;
        long lowRisk = 0;

        Instant now = Instant.now();

        for (Payment payment : payments) {

            if (payment.getStatus() != PaymentStatus.PENDING) {
                continue;
            }

            pendingReceivable++;

            Instant dueDate = payment.getDueDate();

            if (dueDate == null) {
                continue;
            }

            long daysUntilDue = Duration.between(now, dueDate).toDays();

            if (daysUntilDue < 0 || daysUntilDue <= 3) {
                highRisk++;
            } else if (daysUntilDue <= 7) {
                mediumRisk++;
            } else {
                lowRisk++;
            }
        }

        PredictionSummaryDTO summary = new PredictionSummaryDTO();
        summary.setBusinessId(businessId);
        summary.setPendingReceivablePayments(pendingReceivable);
        summary.setHighRiskPayments(highRisk);
        summary.setMediumRiskPayments(mediumRisk);
        summary.setLowRiskPayments(lowRisk);

        summary.setOverallRiskLevel(calculateOverallRisk(highRisk, mediumRisk, lowRisk));
        summary.setPredictionSummary(buildSummary(highRisk, mediumRisk, lowRisk));

        return summary;
    }

    private String calculateOverallRisk(long high, long medium, long low) {
        if (high > medium && high > low) {
            return "high";
        } else if (medium > low) {
            return "medium";
        } else {
            return "low";
        }
    }

    private String buildSummary(long high, long medium, long low) {
        return String.format(
                "The business has %d high-risk, %d medium-risk, and %d low-risk pending payments.",
                high, medium, low
        );
    }
}