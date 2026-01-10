package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.CreatePaymentRequest;
import com.smartpaymentsystem.api.dto.PaymentResponse;
import com.smartpaymentsystem.api.dto.UpdatePaymentRequest;
import com.smartpaymentsystem.api.mapper.PaymentMapper;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/businesses/{businessId}/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponse> getPayments(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId) {
        List<Payment> payments = paymentService.listPayments(ownerId, businessId);

        return payments.stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPaymentById(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long paymentId) {
        Payment payment = paymentService.getPayment(ownerId, businessId, paymentId);

        return PaymentMapper.toResponse(payment);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @Valid @RequestBody CreatePaymentRequest request) {
       Payment payment = paymentService.createPayment(ownerId,
               businessId,
               request.getDirection(),
               request.getAmount(),
               request.getCurrency(),
               request.getDescription(),
               request.getDueDate());

       return PaymentMapper.toResponse(payment);
    }

    @PutMapping("/{paymentId}")
    public PaymentResponse updatePayment(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long paymentId, @Valid @RequestBody UpdatePaymentRequest request) {
        Payment payment = paymentService.updatePayment(ownerId, businessId, paymentId, request.getAmount(), request.getCurrency(), request.getDescription(), request.getDueDate());

        return PaymentMapper.toResponse(payment);
    }

    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long paymentId) {
        paymentService.deletePayment(ownerId, businessId, paymentId);
    }
}
