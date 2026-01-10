package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.BusinessResponseDTO;
import com.smartpaymentsystem.domain.Business;

public class BusinessMapper {

    public static BusinessResponseDTO toResponse(Business business) {
        BusinessResponseDTO response = new BusinessResponseDTO();
        response.setId(business.getId());
        response.setName(business.getName());
        response.setOwnerId(business.getOwner().getId());
        response.setCreatedAt(business.getCreatedAt());
        response.setUpdatedAt(business.getUpdatedAt());

        return response;
    }
}
