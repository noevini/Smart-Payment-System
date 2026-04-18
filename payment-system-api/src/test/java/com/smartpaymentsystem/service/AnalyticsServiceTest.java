package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import com.smartpaymentsystem.domain.*;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock private BusinessAccessService businessAccessService;
    @Mock private PaymentRepository paymentRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Business business;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(1L);
        business.setName("Test Business");
    }

    private Payment makePayment(PaymentDirection direction, PaymentStatus status, double amount) {
        Payment p = new Payment();
        p.setBusiness(business);
        p.setDirection(direction);
        p.setStatus(status);
        p.setAmount(BigDecimal.valueOf(amount));
        p.setCurrency("GBP");
        p.setDueDate(Instant.now());
        return p;
    }

    // ── buildMetrics ──

    @Test
    void buildMetrics_emptyList_returnsAllZeros() {
        when(paymentRepository.findByBusiness_Id(1L)).thenReturn(List.of());

        InsightMetricsDTO result = analyticsService.buildMetrics(1L);

        assertEquals(0L, result.getTotalPayments());
        assertEquals(0L, result.getPaidPayments());
        assertEquals(0L, result.getPendingPayments());
        assertEquals(0L, result.getOverduePayments());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getTotalPendingAmount());
        assertEquals(BigDecimal.ZERO, result.getTotalOverdueAmount());
    }

    @Test
    void buildMetrics_onlyPayablePayments_areIgnored() {
        Payment payable = makePayment(PaymentDirection.PAYABLE, PaymentStatus.PAID, 500);
        when(paymentRepository.findByBusiness_Id(1L)).thenReturn(List.of(payable));

        InsightMetricsDTO result = analyticsService.buildMetrics(1L);

        assertEquals(0L, result.getTotalPayments());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
    }

    @Test
    void buildMetrics_mixedPayments_computesCorrectly() {
        Payment paid = makePayment(PaymentDirection.RECEIVABLE, PaymentStatus.PAID, 200);
        Payment pending = makePayment(PaymentDirection.RECEIVABLE, PaymentStatus.PENDING, 100);
        Payment overdue = makePayment(PaymentDirection.RECEIVABLE, PaymentStatus.OVERDUE, 50);
        Payment payable = makePayment(PaymentDirection.PAYABLE, PaymentStatus.PAID, 999);

        when(paymentRepository.findByBusiness_Id(1L))
                .thenReturn(List.of(paid, pending, overdue, payable));

        InsightMetricsDTO result = analyticsService.buildMetrics(1L);

        assertEquals(3L, result.getTotalPayments());
        assertEquals(1L, result.getPaidPayments());
        assertEquals(1L, result.getPendingPayments());
        assertEquals(1L, result.getOverduePayments());
        assertEquals(0, result.getTotalRevenue().compareTo(BigDecimal.valueOf(200)));
        assertEquals(0, result.getTotalPendingAmount().compareTo(BigDecimal.valueOf(100)));
        assertEquals(0, result.getTotalOverdueAmount().compareTo(BigDecimal.valueOf(50)));
    }

    @Test
    void buildMetrics_multiplePaidPayments_sumsTotalRevenue() {
        Payment p1 = makePayment(PaymentDirection.RECEIVABLE, PaymentStatus.PAID, 300);
        Payment p2 = makePayment(PaymentDirection.RECEIVABLE, PaymentStatus.PAID, 150);
        when(paymentRepository.findByBusiness_Id(1L)).thenReturn(List.of(p1, p2));

        InsightMetricsDTO result = analyticsService.buildMetrics(1L);

        assertEquals(2L, result.getPaidPayments());
        assertEquals(0, result.getTotalRevenue().compareTo(BigDecimal.valueOf(450)));
    }

    @Test
    void buildMetrics_canceledReceivable_doesNotCountInAnyTotal() {
        Payment canceled = makePayment(PaymentDirection.RECEIVABLE, PaymentStatus.CANCELED, 100);
        when(paymentRepository.findByBusiness_Id(1L)).thenReturn(List.of(canceled));

        InsightMetricsDTO result = analyticsService.buildMetrics(1L);

        assertEquals(1L, result.getTotalPayments());
        assertEquals(0L, result.getPaidPayments());
        assertEquals(0L, result.getPendingPayments());
        assertEquals(0L, result.getOverduePayments());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
    }

    @Test
    void buildMetrics_setsBusinessId() {
        when(paymentRepository.findByBusiness_Id(1L)).thenReturn(List.of());

        InsightMetricsDTO result = analyticsService.buildMetrics(1L);

        assertEquals(1L, result.getBusinessId());
    }
}
