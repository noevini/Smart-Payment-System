package com.smartpaymentsystem.api.service;

import com.smartpaymentsystem.api.domain.Business;
import com.smartpaymentsystem.api.domain.User;
import com.smartpaymentsystem.api.repository.BusinessRepository;
import com.smartpaymentsystem.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

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