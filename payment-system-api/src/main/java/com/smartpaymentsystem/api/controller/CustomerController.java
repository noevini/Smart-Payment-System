package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.CustomerRequestDTO;
import com.smartpaymentsystem.api.dto.CustomerResponseDTO;
import com.smartpaymentsystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/businesses/{businessId}/customers")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerResponseDTO> getCustomer(@PathVariable Long businessId, Pageable pageable) {
        return customerService.getCustomers(businessId, pageable);
    }

    @GetMapping("/{customerId}")
    public CustomerResponseDTO getCustomerById(@PathVariable Long businessId, @PathVariable Long customerId) {
        return customerService.getCustomerById(businessId, customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO createCustomer(@PathVariable Long businessId, @Valid @RequestBody CustomerRequestDTO requestDTO) {
        return customerService.createCustomer(businessId, requestDTO);
    }

    @PutMapping("/{customerId}")
    public CustomerResponseDTO updateCustomer(@PathVariable Long businessId, @PathVariable Long customerId, @Valid @RequestBody CustomerRequestDTO requestDTO) {
        return customerService.updateCustomer(businessId, customerId, requestDTO);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long businessId, @PathVariable Long customerId) {
        customerService.deleteCustomer(businessId, customerId);
    }
}
