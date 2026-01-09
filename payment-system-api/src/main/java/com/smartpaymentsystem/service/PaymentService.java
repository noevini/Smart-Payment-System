package com.smartpaymentsystem.service;

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
}
