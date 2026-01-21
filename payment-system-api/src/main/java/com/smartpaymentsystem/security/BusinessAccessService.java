package com.smartpaymentsystem.security;

import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.BusinessRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class BusinessAccessService {
    private final CurrentUserService currentUserService;
    private final BusinessRepository businessRepository;

    public void assertCanAccessBusiness(Long businessId) {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() == UserRole.OWNER) {
            boolean isOwner = businessRepository.existsByIdAndOwners_Id(businessId, user.getId());

            if (!isOwner) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this business");
            }
            return;
        }

        if (user.getRole() == UserRole.STAFF) {
            if (user.getBusiness() == null ||
                    !user.getBusiness().getId().equals(businessId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this business");
            }
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this business");
    }
}
