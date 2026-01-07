package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.BusinessResponse;
import com.smartpaymentsystem.domain.Business;

public class BusinessMapper {

    public static BusinessResponse toResponse(Business business) {
        BusinessResponse response = new BusinessResponse();
        response.setId(business.getId());
        response.setName(business.getName());
        response.setOwnerId(business.getOwner().getId());
        response.setCreatedAt(business.getCreatedAt());
        response.setUpdatedAt(business.getUpdatedAt());

        return response;
    }
}
