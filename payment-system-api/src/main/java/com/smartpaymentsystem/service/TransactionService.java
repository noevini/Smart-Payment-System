package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.TransactionRequestDTO;
import com.smartpaymentsystem.api.dto.TransactionResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.TransactionMapper;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.Transaction;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.TransactionRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
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
    private final BusinessAccessService businessAccessService;

    @Transactional
    public TransactionResponseDTO createTransaction(Long businessId, TransactionRequestDTO requestDTO) {
        businessAccessService.assertCanAccessBusiness(businessId);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        Transaction entity = transactionMapper.toEntity(requestDTO, business);
        Transaction saved = transactionRepository.save(entity);

        return transactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getByBusiness(Long businessId, Pageable pageable) {
        businessAccessService.assertCanAccessBusiness(businessId);
        Page<Transaction> page = transactionRepository.findByBusinessId(businessId, pageable);

        return page.map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO getById(Long transactionId, Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        Transaction transaction = transactionRepository.findByIdAndBusinessId(transactionId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        return  transactionMapper.toResponse(transaction);
    }
}
