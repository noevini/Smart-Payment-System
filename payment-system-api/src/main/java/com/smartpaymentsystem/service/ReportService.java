package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.DashboardResponseDTO;
import com.smartpaymentsystem.domain.PaymentStatus;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.repository.StatusCountRepository;
import com.smartpaymentsystem.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;

    public DashboardResponseDTO getDashboardSummary() {

        Long businessId = currentUserService
                .getCurrentUser()
                .getBusiness()
                .getId();

        List<StatusCountRepository> rows = paymentRepository.countByStatus(businessId);

        Map<PaymentStatus, Long> counts = new EnumMap<>(PaymentStatus.class);
        for (StatusCountRepository row : rows) {
            counts.put(row.getStatus(), row.getCount());
        }

        long paidCount = counts.getOrDefault(PaymentStatus.PAID, 0L);
        long pendingCount = counts.getOrDefault(PaymentStatus.PENDING, 0L);
        long overdueCount = counts.getOrDefault(PaymentStatus.OVERDUE, 0L);
        long canceledCount = counts.getOrDefault(PaymentStatus.CANCELED, 0L);

        long totalPayments = paidCount + pendingCount + overdueCount + canceledCount;

        BigDecimal totalAmount = paymentRepository.sumAmountByBusinessId(businessId);
        BigDecimal paidAmount = paymentRepository
                .sumAmountByBusinessIdAndStatus(businessId, PaymentStatus.PAID);

        BigDecimal outstandingAmount = totalAmount.subtract(paidAmount);
        if (outstandingAmount.signum() < 0) {
            outstandingAmount = BigDecimal.ZERO;
        }

        return new DashboardResponseDTO(
                totalPayments,
                pendingCount,
                paidCount,
                overdueCount,
                canceledCount,
                totalAmount,
                paidAmount,
                outstandingAmount
        );
    }
}
