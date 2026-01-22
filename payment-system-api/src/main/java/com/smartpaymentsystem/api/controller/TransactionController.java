package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.TransactionRequestDTO;
import com.smartpaymentsystem.api.dto.TransactionResponseDTO;
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

    @GetMapping
    public Page<TransactionResponseDTO> getTransactions(@PathVariable Long businessId, Pageable pageable) {
        return transactionService.getByBusiness(businessId, pageable);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponseDTO getTransactionById(@PathVariable Long businessId, @PathVariable Long transactionId ) {
        return transactionService.getById(businessId, transactionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO createTransaction(@PathVariable Long businessId, @Valid @RequestBody TransactionRequestDTO requestDTO) {
        return transactionService.createTransaction(businessId, requestDTO);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponseDTO updateTransaction(@PathVariable Long businessId, @PathVariable Long transactionId, @Valid @RequestBody TransactionRequestDTO requestDTO) {
        return transactionService.updateTransaction(businessId, transactionId, requestDTO);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long businessId, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(businessId, transactionId);
    }
}
