package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.TransactionRequestDTO;
import com.smartpaymentsystem.api.dto.TransactionResponseDTO;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDTO dto, Business business) {
        Transaction transaction = new Transaction();

        transaction.setBusiness(business);
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency().trim().toUpperCase());
        transaction.setDescription(dto.getDescription());
        transaction.setOccurredAt(dto.getOccurredAt());

        return transaction;
    }

    public TransactionResponseDTO toResponse(Transaction transaction) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();

        responseDTO.setId(transaction.getId());
        responseDTO.setBusinessId(transaction.getBusiness().getId());
        responseDTO.setType(transaction.getType());
        responseDTO.setAmount(transaction.getAmount());
        responseDTO.setCurrency(transaction.getCurrency());
        responseDTO.setDescription(transaction.getDescription());
        responseDTO.setOccurredAt(transaction.getOccurredAt());
        responseDTO.setCreatedAt(transaction.getCreatedAt());
        responseDTO.setUpdatedAt(transaction.getUpdatedAt());

        return responseDTO;
    }
}
