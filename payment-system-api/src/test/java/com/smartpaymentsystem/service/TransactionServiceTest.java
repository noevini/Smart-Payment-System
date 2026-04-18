package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.TransactionRequestDTO;
import com.smartpaymentsystem.api.dto.TransactionResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.TransactionMapper;
import com.smartpaymentsystem.domain.*;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.TransactionRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private BusinessRepository businessRepository;
    @Mock private TransactionMapper transactionMapper;
    @Mock private BusinessAccessService businessAccessService;

    @InjectMocks
    private TransactionService transactionService;

    private Business business;
    private Transaction transaction;
    private TransactionResponseDTO responseDTO;
    private TransactionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(1L);
        business.setName("Test Business");

        transaction = Transaction.builder()
                .id(10L)
                .business(business)
                .type(TransactionType.INCOME)
                .amount(BigDecimal.valueOf(500))
                .currency("GBP")
                .description("Salary")
                .occurredAt(Instant.now())
                .build();

        responseDTO = new TransactionResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setBusinessId(1L);
        responseDTO.setType(TransactionType.INCOME);
        responseDTO.setAmount(BigDecimal.valueOf(500));

        requestDTO = new TransactionRequestDTO();
        requestDTO.setType(TransactionType.INCOME);
        requestDTO.setAmount(BigDecimal.valueOf(500));
        requestDTO.setCurrency("GBP");
        requestDTO.setOccurredAt(Instant.now());
    }

    // ── createTransaction ──

    @Test
    void createTransaction_validRequest_savesAndReturnsDTO() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(transactionMapper.toEntity(requestDTO, business)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.createTransaction(1L, requestDTO);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(TransactionType.INCOME, result.getType());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void createTransaction_businessNotFound_throwsResourceNotFoundException() {
        when(businessRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(99L, requestDTO));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_callsAccessCheck() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(transactionMapper.toEntity(any(), any())).thenReturn(transaction);
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(transactionMapper.toResponse(any())).thenReturn(responseDTO);

        transactionService.createTransaction(1L, requestDTO);

        verify(businessAccessService).assertCanAccessBusiness(1L);
    }

    // ── getByBusiness ──

    @Test
    void getByBusiness_returnsPageOfDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findByBusinessId(1L, pageable)).thenReturn(page);
        when(transactionMapper.toResponse(transaction)).thenReturn(responseDTO);

        Page<TransactionResponseDTO> result = transactionService.getByBusiness(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(10L, result.getContent().get(0).getId());
        verify(businessAccessService).assertCanAccessBusiness(1L);
    }

    @Test
    void getByBusiness_emptyPage_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findByBusinessId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<TransactionResponseDTO> result = transactionService.getByBusiness(1L, pageable);

        assertEquals(0, result.getTotalElements());
    }

    // ── getById ──

    @Test
    void getById_found_returnsDTO() {
        when(transactionRepository.findByIdAndBusinessId(10L, 1L))
                .thenReturn(Optional.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.getById(10L, 1L);

        assertEquals(10L, result.getId());
        verify(businessAccessService).assertCanAccessBusiness(1L);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(transactionRepository.findByIdAndBusinessId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getById(99L, 1L));
    }
}
