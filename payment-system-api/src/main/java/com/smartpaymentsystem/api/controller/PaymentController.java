package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.PaymentRequestDTO;
import com.smartpaymentsystem.api.dto.PaymentResponseDTO;
import com.smartpaymentsystem.api.dto.UpdatePaymentRequestDTO;
import com.smartpaymentsystem.api.mapper.PaymentMapper;
import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.security.CurrentUserService;
import com.smartpaymentsystem.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses/{businessId}/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<PaymentResponseDTO> getPayments(@PathVariable Long businessId) {
        Long ownerId = currentUserService.getCurrentUserId();
        List<Payment> payments = paymentService.listPayments(ownerId, businessId);

        return payments.stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDTO getPaymentById(@PathVariable Long businessId, @PathVariable Long paymentId) {
        Long ownerId = currentUserService.getCurrentUserId();
        Payment payment = paymentService.getPayment(ownerId, businessId, paymentId);

        return PaymentMapper.toResponse(payment);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO createPayment(@PathVariable Long businessId, @Valid @RequestBody PaymentRequestDTO request) {
        Long ownerId = currentUserService.getCurrentUserId();
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
    public PaymentResponseDTO updatePayment(@PathVariable Long businessId, @PathVariable Long paymentId, @Valid @RequestBody UpdatePaymentRequestDTO request) {
        Long ownerId = currentUserService.getCurrentUserId();
        Payment payment = paymentService.updatePayment(ownerId, businessId, paymentId, request.getAmount(), request.getCurrency(), request.getDescription(), request.getDueDate());

        return PaymentMapper.toResponse(payment);
    }

    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long businessId, @PathVariable Long paymentId) {
        Long ownerId = currentUserService.getCurrentUserId();
        paymentService.deletePayment(ownerId, businessId, paymentId);
    }
}
