package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.PaymentResponse;
import com.smartpaymentsystem.domain.Payment;

public class PaymentMapper {

    public static PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setBusinessId(payment.getBusiness().getId());
        response.setDirection(payment.getDirection());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setDescription(payment.getDescription());
        response.setDueDate(payment.getDueDate());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());

        return response;
    }
}
