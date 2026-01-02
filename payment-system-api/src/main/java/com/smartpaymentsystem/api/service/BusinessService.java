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

        Business business = new Business();
        business.setOwner(owner);
        business.setName(name);

        return businessRepository.save(business);
    }
}
