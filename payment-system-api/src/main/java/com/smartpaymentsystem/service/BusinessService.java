package com.smartpaymentsystem.service;

import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public List<Business> listBusinessesByOwner(Long ownerId) {
        return businessRepository.findByOwner_Id(ownerId);
    }

    public Business getBusinessByOwner(Long ownerId, Long businessId) {
        return businessRepository.findByIdAndOwner_Id(businessId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found for this owner"));
    }

    public Business createBusiness(Long ownerId, String name) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow();

        String normalisedName = name.trim();

        if (businessRepository.existsByOwner_IdAndName(ownerId, normalisedName)) {
            throw new IllegalArgumentException("Business name already exists for this owner");
        }

        Business business = new Business();
        business.setOwner(owner);
        business.setName(normalisedName);

        return businessRepository.save(business);
    }
}