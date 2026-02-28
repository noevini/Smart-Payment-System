package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.UpdatePaymentRequestDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentDirection;
import com.smartpaymentsystem.domain.PaymentStatus;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BusinessAccessService businessAccessService;
    private final BusinessRepository businessRepository;

    public List<Payment> listPayments(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);
        return paymentRepository.findByBusiness_Id(businessId);
    }

    public Payment getPayment(Long businessId, Long paymentId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        return paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    public Payment createPayment(Long businessId, PaymentDirection direction, BigDecimal amount, String currency, String description, Instant dueDate) {
        businessAccessService.assertCanAccessBusiness(businessId);
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        String normalisedCurrency;
        if (currency == null || currency.trim().isEmpty()) {
            normalisedCurrency = "GBP";
        } else {
            normalisedCurrency = currency.trim().toUpperCase();
        }

        Payment payment = new Payment();
        payment.setBusiness(business);
        payment.setDirection(direction);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(amount);
        payment.setCurrency(normalisedCurrency);
        payment.setDescription(description != null ? description.trim() : null);
        payment.setDueDate(dueDate);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment patchPayment(
            Long businessId,
            Long paymentId,
            UpdatePaymentRequestDTO request
    ) {
        Payment payment = paymentRepository
                .findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (request.getAmount() != null) {
            payment.setAmount(request.getAmount());
        }

        if (request.getCurrency() != null) {
            payment.setCurrency(request.getCurrency());
        }

        if (request.getDescription() != null) {
            payment.setDescription(request.getDescription());
        }

        if (request.getDueDate() != null) {
            payment.setDueDate(request.getDueDate());
        }

        // status transition
        PaymentStatus newStatus = request.getStatus();

        if (newStatus != null && newStatus != payment.getStatus()) {

            PaymentStatus current = payment.getStatus();

            if (current == PaymentStatus.PAID) {
                throw new IllegalStateException("Paid payments cannot change status");
            }

            if (current == PaymentStatus.CANCELED) {
                throw new IllegalStateException("Canceled payments cannot change status");
            }

            boolean allowed =
                    (current == PaymentStatus.PENDING || current == PaymentStatus.OVERDUE)
                            && (newStatus == PaymentStatus.PAID || newStatus == PaymentStatus.CANCELED);

            if (!allowed) {
                throw new IllegalStateException("Invalid payment status transition");
            }

            payment.setStatus(newStatus);

            if (newStatus == PaymentStatus.PAID) {
                payment.setPaidAt(Instant.now());
            }
        }

        return paymentRepository.save(payment);
    }

    public void deletePayment(Long businessId, Long paymentId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        Payment payment = paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("Cannot delete payment");
        }
        paymentRepository.delete(payment);
    }
}
