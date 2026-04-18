package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.CustomerRequestDTO;
import com.smartpaymentsystem.api.dto.CustomerResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.CustomerMapper;
import com.smartpaymentsystem.domain.Customer;
import com.smartpaymentsystem.repository.CustomerRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;
    @Mock private BusinessAccessService businessAccessService;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDTO request;
    private Customer customer;
    private CustomerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        request = new CustomerRequestDTO();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("07700000000");
        request.setNotes("VIP");

        customer = new Customer();
        customer.setId(1L);
        customer.setBusinessId(10L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        responseDTO = new CustomerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("John Doe");
        responseDTO.setEmail("john@example.com");
    }

    // ── createCustomer ──

    @Test
    void createCustomer_newEmail_savesAndReturnsDTO() {
        when(customerRepository.existsByBusinessIdAndEmailIgnoreCase(10L, "john@example.com"))
                .thenReturn(false);
        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.createCustomer(10L, request);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(customerRepository).save(any());
    }

    @Test
    void createCustomer_duplicateEmail_throwsConflictException() {
        when(customerRepository.existsByBusinessIdAndEmailIgnoreCase(10L, "john@example.com"))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> customerService.createCustomer(10L, request));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_nullEmail_skipsEmailCheck() {
        request.setEmail(null);
        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        assertDoesNotThrow(() -> customerService.createCustomer(10L, request));
        verify(customerRepository, never()).existsByBusinessIdAndEmailIgnoreCase(any(), any());
    }

    @Test
    void createCustomer_blankEmail_treatedAsNoEmail() {
        request.setEmail("   ");
        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        assertDoesNotThrow(() -> customerService.createCustomer(10L, request));
        verify(customerRepository, never()).existsByBusinessIdAndEmailIgnoreCase(any(), any());
    }

    // ── getCustomerById ──

    @Test
    void getCustomerById_found_returnsDTO() {
        when(customerRepository.findByIdAndBusinessId(1L, 10L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.getCustomerById(10L, 1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getCustomerById_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findByIdAndBusinessId(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(10L, 99L));
    }

    // ── updateCustomer ──

    @Test
    void updateCustomer_sameEmail_doesNotCheckDuplicate() {
        when(customerRepository.findByIdAndBusinessId(1L, 10L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        request.setEmail("john@example.com");

        customerService.updateCustomer(10L, 1L, request);

        verify(customerRepository, never()).existsByBusinessIdAndEmailIgnoreCase(eq(10L), anyString());
    }

    @Test
    void updateCustomer_changedEmailAlreadyExists_throwsConflictException() {
        when(customerRepository.findByIdAndBusinessId(1L, 10L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByBusinessIdAndEmailIgnoreCase(10L, "other@example.com"))
                .thenReturn(true);

        request.setEmail("other@example.com");

        assertThrows(ConflictException.class,
                () -> customerService.updateCustomer(10L, 1L, request));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_changedEmailNotTaken_updatesSuccessfully() {
        when(customerRepository.findByIdAndBusinessId(1L, 10L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByBusinessIdAndEmailIgnoreCase(10L, "new@example.com"))
                .thenReturn(false);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        request.setEmail("new@example.com");

        assertDoesNotThrow(() -> customerService.updateCustomer(10L, 1L, request));
        verify(customerRepository).save(any());
    }

    @Test
    void updateCustomer_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findByIdAndBusinessId(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.updateCustomer(10L, 99L, request));
    }

    // ── deleteCustomer ──

    @Test
    void deleteCustomer_found_deletesSuccessfully() {
        when(customerRepository.findByIdAndBusinessId(10L, 1L)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.deleteCustomer(10L, 1L));
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findByIdAndBusinessId(10L, 99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(10L, 99L));
        verify(customerRepository, never()).delete(any());
    }
}
