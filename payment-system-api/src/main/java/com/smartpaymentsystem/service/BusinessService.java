package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import com.smartpaymentsystem.security.CurrentUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@Service
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final CurrentUserService currentUserService;
    private final BusinessAccessService businessAccessService;

    public List<Business> listMyBusinesses() {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == UserRole.OWNER) {
            return businessRepository.findByOwners_Id(currentUser.getId());
        }

        if (currentUser.getRole() == UserRole.STAFF) {
            if (currentUser.getBusiness() == null) {
                return List.of();
            }
            return List.of(currentUser.getBusiness());
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this business");
    }

    public Business getBusiness(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }

    public Business createBusiness(String name) {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != UserRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can create");
        }

        String normalisedName = name.trim();

        if (businessRepository.existsByOwners_IdAndName(currentUser.getId(), normalisedName)) {
            throw new ConflictException("Business name already exists for this owner");
        }

        Business business = new Business();
        business.setName(normalisedName);
        business.getOwners().add(currentUser);

        return businessRepository.save(business);
    }

    public Business updateBusiness(Long businessId, String name) {
        businessAccessService.assertCanAccessBusiness(businessId);

        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != UserRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can update business");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        String currentName = business.getName().trim();
        String requestedName = name.trim();

        if (currentName.equals(requestedName)) {
            return business;
        }

        if (businessRepository.existsByOwners_IdAndName(currentUser.getId(), requestedName)) {
            throw new ConflictException("Business name already exists for this owner");
        }

        business.setName(requestedName);
        return businessRepository.save(business);
    }

    public void deleteBusiness(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != UserRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can update business");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        businessRepository.delete(business);
    }
}