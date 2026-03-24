package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.AnalyticsSummaryDTO;
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

        List<Payment> payments = paymentRepository.findByBusiness_Id(businessId);

        AnalyticsSummaryDTO summary = new AnalyticsSummaryDTO();

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

        summary.setBusinessId(businessId);
        summary.setTotalPayments(totalPayments);
        summary.setPaidPayments(paidPayments);
        summary.setPendingPayments(pendingPayments);
        summary.setOverduePayments(overduePayments);
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalPendingAmount(totalPendingAmount);
        summary.setTotalOverdueAmount(totalOverdueAmount);

        return summary;
    }
}