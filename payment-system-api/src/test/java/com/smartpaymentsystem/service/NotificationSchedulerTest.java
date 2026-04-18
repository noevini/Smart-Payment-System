package com.smartpaymentsystem.service;

import com.smartpaymentsystem.domain.*;
import com.smartpaymentsystem.repository.NotificationRepository;
import com.smartpaymentsystem.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    private Business business;
    private Payment overduePayment;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(1L);
        business.setName("Test Business");

        overduePayment = new Payment();
        overduePayment.setId(100L);
        overduePayment.setBusiness(business);
        overduePayment.setStatus(PaymentStatus.PENDING);
        overduePayment.setAmount(BigDecimal.valueOf(250));
        overduePayment.setCurrency("GBP");
        overduePayment.setDescription("Invoice #001");
        overduePayment.setDueDate(Instant.now().minusSeconds(86400));
    }

    // ── generateOverduePaymentNotifications ──

    @Test
    void noOverduePayments_doesNothing() {
        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of());

        notificationScheduler.generateOverduePaymentNotifications();

        verify(paymentRepository, never()).save(any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void overduePayment_updatesStatusToOverdue() {
        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of(overduePayment));
        when(notificationRepository.existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                any(), any(), any(), any())).thenReturn(false);

        notificationScheduler.generateOverduePaymentNotifications();

        assertEquals(PaymentStatus.OVERDUE, overduePayment.getStatus());
        verify(paymentRepository).save(overduePayment);
    }

    @Test
    void overduePayment_noExistingNotification_createsNotification() {
        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of(overduePayment));
        when(notificationRepository.existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                1L, NotificationType.PAYMENT_OVERDUE, "PAYMENT", 100L)).thenReturn(false);

        notificationScheduler.generateOverduePaymentNotifications();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(NotificationType.PAYMENT_OVERDUE, saved.getType());
        assertEquals(business, saved.getBusiness());
        assertEquals("PAYMENT", saved.getRelatedEntityType());
        assertEquals(100L, saved.getRelatedEntityId());
        assertFalse(saved.isRead());
        assertNotNull(saved.getTitle());
        assertNotNull(saved.getMessage());
    }

    @Test
    void overduePayment_notificationAlreadyExists_skipsCreation() {
        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of(overduePayment));
        when(notificationRepository.existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                1L, NotificationType.PAYMENT_OVERDUE, "PAYMENT", 100L)).thenReturn(true);

        notificationScheduler.generateOverduePaymentNotifications();

        verify(notificationRepository, never()).save(any());
        verify(paymentRepository).save(overduePayment);
    }

    @Test
    void multipleOverduePayments_processesEachIndependently() {
        Payment payment2 = new Payment();
        payment2.setId(200L);
        payment2.setBusiness(business);
        payment2.setStatus(PaymentStatus.PENDING);
        payment2.setAmount(BigDecimal.valueOf(100));
        payment2.setCurrency("GBP");
        payment2.setDueDate(Instant.now().minusSeconds(3600));

        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of(overduePayment, payment2));
        when(notificationRepository.existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                eq(1L), eq(NotificationType.PAYMENT_OVERDUE), eq("PAYMENT"), anyLong()))
                .thenReturn(false);

        notificationScheduler.generateOverduePaymentNotifications();

        verify(paymentRepository, times(2)).save(any());
        verify(notificationRepository, times(2)).save(any());
        assertEquals(PaymentStatus.OVERDUE, overduePayment.getStatus());
        assertEquals(PaymentStatus.OVERDUE, payment2.getStatus());
    }

    @Test
    void overduePayment_notificationMessageContainsAmountAndCurrency() {
        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of(overduePayment));
        when(notificationRepository.existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                any(), any(), any(), any())).thenReturn(false);

        notificationScheduler.generateOverduePaymentNotifications();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        String message = captor.getValue().getMessage();
        assertTrue(message.contains("250"));
        assertTrue(message.contains("GBP"));
    }

    @Test
    void overduePayment_nullDescription_usesDefaultInMessage() {
        overduePayment.setDescription(null);

        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of(overduePayment));
        when(notificationRepository.existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                any(), any(), any(), any())).thenReturn(false);

        notificationScheduler.generateOverduePaymentNotifications();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertTrue(captor.getValue().getMessage().contains("No description"));
    }

    // ── runOnStartup ──

    @Test
    void runOnStartup_triggersOverdueCheck() {
        when(paymentRepository.findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any()))
                .thenReturn(List.of());

        notificationScheduler.runOnStartup();

        verify(paymentRepository).findByStatusAndDueDateBefore(eq(PaymentStatus.PENDING), any());
    }
}
