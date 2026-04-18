package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.UserResponseDTO;
import com.smartpaymentsystem.api.mapper.UserMapper;
import com.smartpaymentsystem.security.BusinessAccessService;
import com.smartpaymentsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses/{businessId}/staff")
@AllArgsConstructor
public class StaffController {

    private final UserService userService;
    private final BusinessAccessService businessAccessService;

    @GetMapping
    public List<UserResponseDTO> listStaff(@PathVariable Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);
        return userService.listStaffByBusiness(businessId)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStaff(@PathVariable Long businessId, @PathVariable Long userId) {
        businessAccessService.assertCanAccessBusiness(businessId);
        userService.deleteStaffUser(businessId, userId);
    }
}
