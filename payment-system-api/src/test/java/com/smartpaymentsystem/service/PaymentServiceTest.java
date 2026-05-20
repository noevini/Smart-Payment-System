package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.UpdatePaymentRequestDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.*;
import com.smartpaymentsystem.repository.BusinessRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BusinessAccessService businessAccessService;
    @Mock private BusinessRepository businessRepository;
    @Mock private com.smartpaymentsystem.repository.CustomerRepository customerRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Business business;
    private Payment payment;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(1L);
        business.setName("Test Business");

        payment = new Payment();
        payment.setId(10L);
        payment.setBusiness(business);
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setCurrency("GBP");
        payment.setDueDate(Instant.now().plusSeconds(3600));
    }

    // ── applyStatusTransition (tested via patchPayment) ──

    @Test
    void pendingToPaid_setsStatusAndPaidAt() {
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.PAID);

        Payment result = paymentService.patchPayment(1L, 10L, req);

        assertEquals(PaymentStatus.PAID, result.getStatus());
        assertNotNull(result.getPaidAt());
    }

    @Test
    void pendingToCanceled_setsStatusCanceled() {
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.CANCELED);

        Payment result = paymentService.patchPayment(1L, 10L, req);

        assertEquals(PaymentStatus.CANCELED, result.getStatus());
        assertNull(result.getPaidAt());
    }

    @Test
    void overdueToPaid_setsStatusPaid() {
        payment.setStatus(PaymentStatus.OVERDUE);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.PAID);

        Payment result = paymentService.patchPayment(1L, 10L, req);

        assertEquals(PaymentStatus.PAID, result.getStatus());
        assertNotNull(result.getPaidAt());
    }

    @Test
    void overdueToCanceled_setsStatusCanceled() {
        payment.setStatus(PaymentStatus.OVERDUE);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.CANCELED);

        Payment result = paymentService.patchPayment(1L, 10L, req);

        assertEquals(PaymentStatus.CANCELED, result.getStatus());
    }

    @Test
    void paidToCanceled_throwsConflictException() {
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.CANCELED);

        assertThrows(ConflictException.class, () -> paymentService.patchPayment(1L, 10L, req));
    }

    @Test
    void canceledToPaid_throwsConflictException() {
        payment.setStatus(PaymentStatus.CANCELED);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.PAID);

        assertThrows(ConflictException.class, () -> paymentService.patchPayment(1L, 10L, req));
    }

    @Test
    void paidToPending_throwsConflictException() {
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.PENDING);

        assertThrows(ConflictException.class, () -> paymentService.patchPayment(1L, 10L, req));
    }

    // ── patchPayment field updates ──

    @Test
    void patchPayment_updatesAmountAndCurrency() {
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setAmount(BigDecimal.valueOf(999));
        req.setCurrency("usd");

        Payment result = paymentService.patchPayment(1L, 10L, req);

        assertEquals(BigDecimal.valueOf(999), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void patchPayment_sameStatus_doesNotCallTransition() {
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdatePaymentRequestDTO req = new UpdatePaymentRequestDTO();
        req.setStatus(PaymentStatus.PENDING);

        Payment result = paymentService.patchPayment(1L, 10L, req);

        assertEquals(PaymentStatus.PENDING, result.getStatus());
    }

    // ── deletePayment ──

    @Test
    void deletePayment_pendingPayment_deletesSuccessfully() {
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        assertDoesNotThrow(() -> paymentService.deletePayment(1L, 10L));
        verify(paymentRepository).delete(payment);
    }

    @Test
    void deletePayment_paidPayment_throwsConflictException() {
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        assertThrows(ConflictException.class, () -> paymentService.deletePayment(1L, 10L));
        verify(paymentRepository, never()).delete(any());
    }

    @Test
    void deletePayment_overduePayment_throwsConflictException() {
        payment.setStatus(PaymentStatus.OVERDUE);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        assertThrows(ConflictException.class, () -> paymentService.deletePayment(1L, 10L));
        verify(paymentRepository, never()).delete(any());
    }

    @Test
    void deletePayment_canceledPayment_throwsConflictException() {
        payment.setStatus(PaymentStatus.CANCELED);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        assertThrows(ConflictException.class, () -> paymentService.deletePayment(1L, 10L));
    }

    @Test
    void deletePayment_notFound_throwsResourceNotFoundException() {
        when(paymentRepository.findByIdAndBusiness_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.deletePayment(1L, 99L));
    }

    // ── getPayment ──

    @Test
    void getPayment_found_returnsPayment() {
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdAndBusiness_Id(10L, 1L)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPayment(1L, 10L);

        assertEquals(10L, result.getId());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
    }

    @Test
    void getPayment_notFound_throwsResourceNotFoundException() {
        when(paymentRepository.findByIdAndBusiness_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPayment(1L, 99L));
    }

    // ── createPayment ──

    @Test
    void createPayment_setsPendingStatusAndSaves() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.createPayment(
                1L, BigDecimal.valueOf(250), "GBP",
                "Invoice", Instant.now().plusSeconds(86400), null
        );

        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.valueOf(250), result.getAmount());
        assertEquals("GBP", result.getCurrency());
        verify(paymentRepository).save(any());
    }

    @Test
    void createPayment_nullCurrency_defaultsToGBP() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.createPayment(
                1L, BigDecimal.valueOf(100), null, "desc", Instant.now().plusSeconds(3600), null
        );

        assertEquals("GBP", result.getCurrency());
    }

    @Test
    void createPayment_blankCurrency_defaultsToGBP() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.createPayment(
                1L, BigDecimal.valueOf(100), "   ", "desc", Instant.now().plusSeconds(3600), null
        );

        assertEquals("GBP", result.getCurrency());
    }

    @Test
    void createPayment_businessNotFound_throwsResourceNotFoundException() {
        when(businessRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.createPayment(99L,
                        BigDecimal.valueOf(100), "GBP", "desc", Instant.now().plusSeconds(3600), null)
        );
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_lowercaseCurrency_isUppercased() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.createPayment(
                1L, BigDecimal.valueOf(50), "eur", "expense", Instant.now().plusSeconds(3600), null
        );

        assertEquals("EUR", result.getCurrency());
    }
}
