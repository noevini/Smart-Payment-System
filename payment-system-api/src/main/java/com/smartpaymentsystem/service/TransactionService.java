package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.TransactionRequestDTO;
import com.smartpaymentsystem.api.dto.TransactionResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.TransactionMapper;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.Transaction;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BusinessRepository businessRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponseDTO createTransaction(Long businessId, TransactionRequestDTO requestDTO) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        Transaction entity = transactionMapper.toEntity(requestDTO, business);
        Transaction saved = transactionRepository.save(entity);

        return transactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getByBusiness(Long businessId, Pageable pageable) {
        Page<Transaction> page = transactionRepository.findByBusinessId(businessId, pageable);

        return page.map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO getById(Long transactionId, Long businessId) {
        Transaction transaction = transactionRepository.findByIdAndBusinessId(transactionId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        return  transactionMapper.toResponse(transaction);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Long transactionId, Long businessId, TransactionRequestDTO requestDTO) {
        Transaction transaction = transactionRepository.findByIdAndBusinessId(transactionId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transaction.setType(requestDTO.getType());
        transaction.setAmount(requestDTO.getAmount());
        transaction.setCurrency(requestDTO.getCurrency().trim().toUpperCase());
        transaction.setDescription(requestDTO.getDescription());
        transaction.setOccurredAt(requestDTO.getOccurredAt());

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long businessId) {
        Transaction transaction = transactionRepository.findByIdAndBusinessId(transactionId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
    }
}
