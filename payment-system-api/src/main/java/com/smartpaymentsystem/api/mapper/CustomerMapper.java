package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.CustomerRequestDTO;
import com.smartpaymentsystem.api.dto.CustomerResponseDTO;
import com.smartpaymentsystem.domain.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequestDTO requestDTO) {
        Customer customer = new Customer();

        customer.setName(requestDTO.getName());
        customer.setEmail(requestDTO.getEmail().trim().toLowerCase());
        customer.setPhone(requestDTO.getPhone());
        customer.setNotes(requestDTO.getNotes());

        return customer;
    }

    public CustomerResponseDTO toResponse(Customer customer) {
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();

        customerResponseDTO.setId(customer.getId());
        customerResponseDTO.setName(customer.getName());
        customerResponseDTO.setEmail(customer.getEmail().trim().toLowerCase());
        customerResponseDTO.setPhone(customer.getPhone());
        customerResponseDTO.setNotes(customer.getNotes());
        customerResponseDTO.setCreatedAt(customer.getCreatedAt());
        customerResponseDTO.setUpdatedAt(customer.getUpdatedAt());

        return customerResponseDTO;
    }
}
