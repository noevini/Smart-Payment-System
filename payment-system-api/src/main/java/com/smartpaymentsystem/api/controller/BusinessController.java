package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.BusinessResponseDTO;
import com.smartpaymentsystem.api.dto.BusinessRequestDTO;
import com.smartpaymentsystem.api.dto.UpdateBusinessRequestDTO;
import com.smartpaymentsystem.api.mapper.BusinessMapper;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.service.BusinessService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@AllArgsConstructor
public class BusinessController {
    private final BusinessService businessService;

    @GetMapping
    public List<BusinessResponseDTO> findByOwnerId(@RequestHeader("X-User-Id") Long ownerId) {
        List<Business> businesses = businessService.listBusinessesByOwner(ownerId);

        return businesses.stream()
                .map(BusinessMapper::toResponse)
                .toList();
    }

    @GetMapping("/{businessId}")
    public BusinessResponseDTO getById(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId) {
        Business business = businessService.getBusinessByOwner(ownerId, businessId);

        return BusinessMapper.toResponse(business);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessResponseDTO createBusiness(@RequestHeader("X-User-Id") Long ownerId, @Valid @RequestBody BusinessRequestDTO request) {
        Business business = businessService.createBusiness(ownerId, request.getName());

        return BusinessMapper.toResponse(business);
    }

    @PutMapping("/{businessId}")
    public BusinessResponseDTO updateBusiness(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId, @Valid @RequestBody UpdateBusinessRequestDTO request) {
        Business business = businessService.updateBusiness(ownerId, businessId, request.getName());

        return BusinessMapper.toResponse(business);
    }

    @DeleteMapping("/{businessId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBusiness(@RequestHeader("X-User-Id") Long ownerId, @PathVariable Long businessId) {
        businessService.deleteBusiness(ownerId, businessId);
    }
}
