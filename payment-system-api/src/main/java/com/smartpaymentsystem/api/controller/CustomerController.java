package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.CustomerRequestDTO;
import com.smartpaymentsystem.api.dto.CustomerResponseDTO;
import com.smartpaymentsystem.service.BusinessService;
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
    private final BusinessService businessService;

    @GetMapping
    public Page<CustomerResponseDTO> getCustomer(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, Pageable pageable) {
        validateOwnership(ownerId, businessId);
        return customerService.getCustomers(businessId, pageable);
    }

    @GetMapping("/{customerId}")
    public CustomerResponseDTO getCustomerById(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long customerId) {
        validateOwnership(ownerId, businessId);
        return customerService.getCustomerById(businessId, customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO createCustomer(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @Valid @RequestBody CustomerRequestDTO requestDTO) {
        validateOwnership(ownerId, businessId);
        return customerService.createCustomer(businessId, requestDTO);
    }

    @PutMapping("/{customerId}")
    public CustomerResponseDTO updateCustomer(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long customerId, @Valid @RequestBody CustomerRequestDTO requestDTO) {
        validateOwnership(ownerId, businessId);
        return customerService.updateCustomer(businessId, customerId, requestDTO);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @PathVariable Long customerId) {
        validateOwnership(ownerId, businessId);
        customerService.deleteCustomer(businessId, customerId);
    }

    private void validateOwnership(Long ownerId, Long businessId) {
        businessService.getBusinessByOwner(ownerId, businessId);
    }
}
