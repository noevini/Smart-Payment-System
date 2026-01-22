package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.PaymentRequestDTO;
import com.smartpaymentsystem.api.dto.PaymentResponseDTO;
import com.smartpaymentsystem.api.dto.UpdatePaymentRequestDTO;
import com.smartpaymentsystem.api.mapper.PaymentMapper;
import com.smartpaymentsystem.domain.Payment;
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

    @GetMapping
    public List<PaymentResponseDTO> getPayments(@PathVariable Long businessId) {
        List<Payment> payments = paymentService.listPayments(businessId);

        return payments.stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDTO getPaymentById(@PathVariable Long businessId, @PathVariable Long paymentId) {
        Payment payment = paymentService.getPayment(businessId, paymentId);

        return PaymentMapper.toResponse(payment);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO createPayment(@PathVariable Long businessId, @Valid @RequestBody PaymentRequestDTO request) {
        Payment payment = paymentService.createPayment(businessId,
               request.getDirection(),
               request.getAmount(),
               request.getCurrency(),
               request.getDescription(),
               request.getDueDate());

       return PaymentMapper.toResponse(payment);
    }

    @PutMapping("/{paymentId}")
    public PaymentResponseDTO updatePayment(@PathVariable Long businessId, @PathVariable Long paymentId, @Valid @RequestBody UpdatePaymentRequestDTO request) {
        Payment payment = paymentService.updatePayment(businessId, paymentId, request.getAmount(), request.getCurrency(), request.getDescription(), request.getDueDate());

        return PaymentMapper.toResponse(payment);
    }

    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long businessId, @PathVariable Long paymentId) {
        paymentService.deletePayment(businessId, paymentId);
    }
}
