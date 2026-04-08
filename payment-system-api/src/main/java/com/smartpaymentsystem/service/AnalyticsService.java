package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.AnalyticsSummaryDTO;
import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentDirection;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class AnalyticsService {

    private final BusinessAccessService businessAccessService;
    private final PaymentRepository paymentRepository;

    public AnalyticsSummaryDTO getSummary(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        InsightMetricsDTO metrics = buildMetrics(businessId);

        AnalyticsSummaryDTO summary = new AnalyticsSummaryDTO();
        summary.setBusinessId(businessId);
        summary.setTotalPayments(metrics.getTotalPayments());
        summary.setPaidPayments(metrics.getPaidPayments());
        summary.setPendingPayments(metrics.getPendingPayments());
        summary.setOverduePayments(metrics.getOverduePayments());
        summary.setTotalRevenue(metrics.getTotalRevenue());
        summary.setTotalPendingAmount(metrics.getTotalPendingAmount());
        summary.setTotalOverdueAmount(metrics.getTotalOverdueAmount());

        return summary;
    }

    public InsightMetricsDTO buildMetrics(Long businessId) {
        List<Payment> payments = paymentRepository.findByBusiness_Id(businessId);

        long totalPayments = 0;
        long paidPayments = 0;
        long pendingPayments = 0;
        long overduePayments = 0;

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalPendingAmount = BigDecimal.ZERO;
        BigDecimal totalOverdueAmount = BigDecimal.ZERO;

        for (Payment payment : payments) {
            if (payment.getDirection() != PaymentDirection.RECEIVABLE) continue;

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

        InsightMetricsDTO metrics = new InsightMetricsDTO();
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