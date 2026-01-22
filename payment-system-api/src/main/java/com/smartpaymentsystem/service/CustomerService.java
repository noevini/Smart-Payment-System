package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.CustomerRequestDTO;
import com.smartpaymentsystem.api.dto.CustomerResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.CustomerMapper;
import com.smartpaymentsystem.domain.Customer;
import com.smartpaymentsystem.repository.CustomerRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final BusinessAccessService businessAccessService;

    @Transactional
    public CustomerResponseDTO createCustomer(Long businessId, CustomerRequestDTO requestDTO) {
        businessAccessService.assertCanAccessBusiness(businessId);

        String email = requestDTO.getEmail();

        if (email != null) {
            email = email.trim();

            if (email.isEmpty()) {
                email = null;
            }
        }

        if (email != null) {
            boolean exists = customerRepository.existsByBusinessIdAndEmailIgnoreCase(businessId, email);

            if (exists) {
                throw new ConflictException("Email already registered");
            }
        }

        Customer customer = customerMapper.toEntity(requestDTO);
        customer.setEmail(email);
        customer.setBusinessId(businessId);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getCustomers(Long businessId, Pageable pageable) {
        businessAccessService.assertCanAccessBusiness(businessId);
        Page<Customer> page = customerRepository.findByBusinessId(businessId, pageable);

        return page.map(customerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long businessId, Long customerId) {
        Customer customer = customerRepository.findByIdAndBusinessId(customerId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return customerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long businessId, Long customerId, CustomerRequestDTO requestDTO) {
        businessAccessService.assertCanAccessBusiness(businessId);
        Customer customer = customerRepository.findByIdAndBusinessId(customerId, businessId)
               .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        String email = requestDTO.getEmail();

        if (email != null) {
            email = email.trim();

            if (email.isEmpty()) {
                email = null;
            }
        }

        if (email != null) {
            boolean changed = !email.equalsIgnoreCase(customer.getEmail());

            if (changed) {
                boolean exists = customerRepository.existsByBusinessIdAndEmailIgnoreCase(businessId, email);

                if (exists) {
                    throw new ConflictException("Email already registered");
                }
            }
        }

        customer.setName(requestDTO.getName());
        customer.setEmail(email);
        customer.setPhone(requestDTO.getPhone());
        customer.setNotes(requestDTO.getNotes());

        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    @Transactional
    public void deleteCustomer(Long customerId, Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);
        Customer customer = customerRepository.findByIdAndBusinessId(customerId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerRepository.delete(customer);
    }
}
