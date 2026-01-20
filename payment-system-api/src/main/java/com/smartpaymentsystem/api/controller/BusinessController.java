package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.BusinessResponseDTO;
import com.smartpaymentsystem.api.dto.BusinessRequestDTO;
import com.smartpaymentsystem.api.dto.UpdateBusinessRequestDTO;
import com.smartpaymentsystem.api.mapper.BusinessMapper;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.security.CurrentUserService;
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
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<BusinessResponseDTO> findByOwnerId() {
        Long ownerId = currentUserService.getCurrentUserId();
        List<Business> businesses = businessService.listBusinessesByOwner(ownerId);

        return businesses.stream()
                .map(BusinessMapper::toResponse)
                .toList();
    }

    @GetMapping("/{businessId}")
    public BusinessResponseDTO getById(@PathVariable Long businessId) {
        Long ownerId = currentUserService.getCurrentUserId();
        Business business = businessService.getBusinessByOwner(ownerId, businessId);

        return BusinessMapper.toResponse(business);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessResponseDTO createBusiness(@Valid @RequestBody BusinessRequestDTO request) {
        Long ownerId = currentUserService.getCurrentUserId();
        Business business = businessService.createBusiness(ownerId, request.getName());

        return BusinessMapper.toResponse(business);
    }

    @PutMapping("/{businessId}")
    public BusinessResponseDTO updateBusiness(@PathVariable Long businessId, @Valid @RequestBody UpdateBusinessRequestDTO request) {
        Long ownerId = currentUserService.getCurrentUserId();
        Business business = businessService.updateBusiness(ownerId, businessId, request.getName());

        return BusinessMapper.toResponse(business);
    }

    @DeleteMapping("/{businessId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBusiness(@PathVariable Long businessId) {
        Long ownerId = currentUserService.getCurrentUserId();
        businessService.deleteBusiness(ownerId, businessId);
    }
}
