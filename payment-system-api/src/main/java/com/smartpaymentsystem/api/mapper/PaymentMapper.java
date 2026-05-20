package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.PaymentResponseDTO;
import com.smartpaymentsystem.domain.Payment;

public class PaymentMapper {

    public static PaymentResponseDTO toResponse(Payment payment) {
        PaymentResponseDTO response = new PaymentResponseDTO();

        response.setId(payment.getId());
        response.setBusinessId(payment.getBusiness().getId());
        if (payment.getCustomer() != null) {
            response.setCustomerId(payment.getCustomer().getId());
            response.setCustomerName(payment.getCustomer().getName());
        }
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
