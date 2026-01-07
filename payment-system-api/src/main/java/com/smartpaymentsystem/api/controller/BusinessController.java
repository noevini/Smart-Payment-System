package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.BusinessResponse;
import com.smartpaymentsystem.api.dto.CreateBusinessRequest;
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
    public List<BusinessResponse> findByOwnerId(@RequestHeader("X-User-Id") Long ownerId) {
        List<Business> businesses = businessService.listBusinessesByOwner(ownerId);

        return businesses.stream()
                .map(BusinessMapper::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessResponse createBusiness(@Valid @RequestBody CreateBusinessRequest request) {
        Business business = businessService.createBusiness(request.getOwnerId(), request.getName());

        BusinessResponse businessResponse = new BusinessResponse();
        businessResponse.setId(business.getId());
        businessResponse.setName(business.getName());
        businessResponse.setOwnerId(business.getOwner().getId());
        businessResponse.setCreatedAt(business.getCreatedAt());
        businessResponse.setUpdatedAt(business.getUpdatedAt());

        return businessResponse;
    }
}
