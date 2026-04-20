package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.UpdatePaymentRequestDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.*;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<Payment> listPayments(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);
        return paymentRepository.findByBusiness_Id(businessId);
    }

    @Transactional(readOnly = true)
    public Payment getPayment(Long businessId, Long paymentId) {
        businessAccessService.assertCanAccessBusiness(businessId);
        return paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Transactional
    public Payment createPayment(Long businessId, PaymentDirection direction, BigDecimal amount,
                                 String currency, String description, Instant dueDate,
                                 Long customerId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        String normalisedCurrency = (currency == null || currency.trim().isEmpty())
                ? "GBP"
                : currency.trim().toUpperCase();

        Payment payment = new Payment();
        payment.setBusiness(business);
        payment.setDirection(direction);
        payment.setStatus(dueDate != null && dueDate.isBefore(Instant.now())
                ? PaymentStatus.OVERDUE
                : PaymentStatus.PENDING);
        payment.setAmount(amount);
        payment.setCurrency(normalisedCurrency);
        payment.setDescription(description != null ? description.trim() : null);
        payment.setDueDate(dueDate);

        if (customerId != null) {
            Customer customer = customerRepository.findByIdAndBusinessId(customerId, businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            payment.setCustomer(customer);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment patchPayment(Long businessId, Long paymentId, UpdatePaymentRequestDTO request) {
        businessAccessService.assertCanAccessBusiness(businessId);

        Payment payment = paymentRepository
                .findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (request.getAmount() != null) {
            payment.setAmount(request.getAmount());
        }
        if (request.getCurrency() != null) {
            payment.setCurrency(request.getCurrency().trim().toUpperCase());
        }
        if (request.getDescription() != null) {
            payment.setDescription(request.getDescription().trim());
        }
        if (request.getDueDate() != null) {
            payment.setDueDate(request.getDueDate());
            if (payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.OVERDUE) {
                payment.setStatus(request.getDueDate().isBefore(Instant.now())
                        ? PaymentStatus.OVERDUE
                        : PaymentStatus.PENDING);
            }
        }
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findByIdAndBusinessId(request.getCustomerId(), businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            payment.setCustomer(customer);
        }

        PaymentStatus newStatus = request.getStatus();
        if (newStatus != null && newStatus != payment.getStatus()) {
            applyStatusTransition(payment, newStatus);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(Long businessId, Long paymentId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        Payment payment = paymentRepository.findByIdAndBusiness_Id(paymentId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException(
                    "Cannot delete payment with status: " + payment.getStatus()
                            + ". Only PENDING payments can be deleted."
            );
        }

        paymentRepository.delete(payment);
    }

    private void applyStatusTransition(Payment payment, PaymentStatus newStatus) {
        PaymentStatus current = payment.getStatus();

        if (current == PaymentStatus.PAID) {
            throw new ConflictException("Paid payments cannot change status");
        }
        if (current == PaymentStatus.CANCELED) {
            throw new ConflictException("Cancelled payments cannot change status");
        }

        boolean allowed = (current == PaymentStatus.PENDING || current == PaymentStatus.OVERDUE)
                && (newStatus == PaymentStatus.PAID || newStatus == PaymentStatus.CANCELED);

        if (!allowed) {
            throw new ConflictException(
                    "Invalid status transition from " + current + " to " + newStatus
            );
        }

        payment.setStatus(newStatus);
        if (newStatus == PaymentStatus.PAID) {
            payment.setPaidAt(Instant.now());
        }
    }
}
