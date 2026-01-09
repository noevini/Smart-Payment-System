package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentDirection;
import com.smartpaymentsystem.domain.PaymentStatus;
import com.smartpaymentsystem.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BusinessService businessService;


    public List<Payment> listPayments(Long ownerId, Long businessId) {
        businessService.getBusinessByOwner(ownerId, businessId);

        return paymentRepository.findByBusiness_Id(businessId);
    }

    public Payment getPayment(Long ownerId, Long businessId, Long paymentId) {
        businessService.getBusinessByOwner(ownerId, businessId);

        return paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    public Payment createPayment(Long ownerId, Long businessId, PaymentDirection direction, BigDecimal amount, String currency, String description, Instant dueDate) {
        Business business = businessService.getBusinessByOwner(ownerId, businessId);

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

    public Payment updatePayment(Long ownerId, Long businessId, Long paymentId, BigDecimal amount, String currency, String description, Instant dueDate) {
        businessService.getBusinessByOwner(ownerId, businessId);
        Payment payment = paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("Cannot change the payment");
        }

        boolean changed = false;

        if (amount != null && !amount.equals(payment.getAmount())) {
            payment.setAmount(amount);
            changed = true;
        }

        if (currency != null && !currency.isBlank()) {
            String normalisedCurrency = currency.trim().toUpperCase();
            if (!normalisedCurrency.equals(payment.getCurrency())) {
                payment.setCurrency(normalisedCurrency);
                changed = true;
            }
        }

        if (description != null) {
            String normalisedDescription = description.trim();

            if (normalisedDescription.isBlank()) {
                normalisedDescription = null;
            }

            assert normalisedDescription != null;
            if (!normalisedDescription.equals(payment.getDescription())) {
                payment.setDescription(normalisedDescription);
                changed = true;
            }
        }

        if (dueDate != null && !dueDate.equals(payment.getDueDate())) {
            payment.setDueDate(dueDate);
            changed = true;
        }

        if (!changed) {
            return payment;
        }

        return paymentRepository.save(payment);
    }

    public void deletePayment(Long ownerId, Long businessId, Long paymentId) {
        businessService.getBusinessByOwner(ownerId, businessId);
        Payment payment = paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("Cannot delete payment");
        }

        paymentRepository.delete(payment);
    }
}
