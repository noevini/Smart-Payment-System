package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
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
        return businessRepository.findByOwners_Id(ownerId);
    }

    public Business getBusinessByOwner(Long ownerId, Long businessId) {
        return businessRepository.findByIdAndOwners_Id(businessId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found for this owner"));
    }

    public Business createBusiness(Long ownerId, String name) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (owner.getRole() != UserRole.OWNER) {
            throw new ConflictException("Only owners can create businesses");
        }

        String normalisedName = name.trim();

        if (businessRepository.existsByOwners_IdAndName(ownerId, normalisedName)) {
            throw new ConflictException("Business name already exists for this owner");
        }

        Business business = new Business();
        business.getOwners().add(owner);
        business.setName(normalisedName);

        return businessRepository.save(business);
    }

    public Business updateBusiness(Long ownerId, Long businessId, String name) {
        Business business = businessRepository.findByIdAndOwners_Id(businessId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        String currentName = business.getName().trim();
        String requestedName = name.trim();

        if (currentName.equals(requestedName)) {
            return business;
        }

        if (businessRepository.existsByOwners_IdAndName(ownerId, requestedName)) {
            throw new ConflictException("Business name already exists for this owner");
        }

        business.setName(requestedName);
        return businessRepository.save(business);
    }

    public void deleteBusiness(Long ownerId, Long businessId) {
        Business business = businessRepository.findByIdAndOwners_Id(businessId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        businessRepository.delete(business);
    }
}