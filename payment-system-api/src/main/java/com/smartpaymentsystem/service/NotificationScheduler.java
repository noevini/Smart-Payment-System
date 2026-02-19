package com.smartpaymentsystem.service;

import com.smartpaymentsystem.domain.Notification;
import com.smartpaymentsystem.domain.NotificationType;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentStatus;
import com.smartpaymentsystem.repository.NotificationRepository;
import com.smartpaymentsystem.repository.PaymentRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {
    private static final String RELATED_ENTITY_TYPE_PAYMENT = "PAYMENT";
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 8 * * *")
    public void generateOverduePaymentNotifications() {
        Instant now = Instant.now();

        List<Payment> overduePayments =
                paymentRepository.findByStatusAndDueDate(
                        PaymentStatus.PENDING,
                        now
                );

        for (Payment payment : overduePayments) {

            payment.setStatus(PaymentStatus.OVERDUE);
            paymentRepository.save(payment);

            Long businessId = payment.getBusiness().getId();

            boolean alreadyExists = notificationRepository
                    .existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
                            businessId,
                            NotificationType.PAYMENT_OVERDUE,
                            RELATED_ENTITY_TYPE_PAYMENT,
                            payment.getId()
                    );

            if (alreadyExists) continue;

            Notification notification = new Notification();
            notification.setBusiness(payment.getBusiness());
            notification.setType(NotificationType.PAYMENT_OVERDUE);
            notification.setTitle("Payment overdue");
            notification.setMessage(buildOverdueMessage(payment));
            notification.setRelatedEntityType(RELATED_ENTITY_TYPE_PAYMENT);
            notification.setRelatedEntityId(payment.getId());
            notification.setRead(false);

            notificationRepository.save(notification);
        }
    }

    private String buildOverdueMessage(Payment payment) {
        String desc = payment.getDescription() != null
                ? payment.getDescription()
                : "No description";

        return "Overdue payment: "
                + payment.getAmount() + " "
                + payment.getCurrency()
                + " (due " + payment.getDueDate() + ") - "
                + desc;
    }
}
