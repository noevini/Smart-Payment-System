package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.TransactionRequestDTO;
import com.smartpaymentsystem.api.dto.TransactionResponseDTO;
import com.smartpaymentsystem.service.BusinessService;
import com.smartpaymentsystem.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/businesses/{businessId}/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final BusinessService businessService;

    @GetMapping
    public Page<TransactionResponseDTO> getTransactions(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, Pageable pageable) {
        validateOwnership(ownerId, businessId);
        return transactionService.getByBusiness(businessId, pageable);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponseDTO getTransactionById(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long transactionId ) {
        validateOwnership(ownerId, businessId);
        return transactionService.getById(businessId, transactionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO createTransaction(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @Valid @RequestBody TransactionRequestDTO requestDTO) {
        validateOwnership(ownerId, businessId);
        return transactionService.createTransaction(businessId, requestDTO);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponseDTO updateTransaction(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long transactionId, @Valid @RequestBody TransactionRequestDTO requestDTO) {
        validateOwnership(ownerId, businessId);
        return transactionService.updateTransaction(businessId, transactionId, requestDTO);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long transactionId) {
        validateOwnership(ownerId, businessId);
        transactionService.deleteTransaction(businessId, transactionId);
    }

    private void validateOwnership(Long ownerId, Long businessId) {
        businessService.getBusinessByOwner(ownerId, businessId);
    }
}
