package com.smartpaymentsystem.service;

import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.UserRepository;
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