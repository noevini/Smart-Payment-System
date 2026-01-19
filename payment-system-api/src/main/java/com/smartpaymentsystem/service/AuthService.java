package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.LoginRequestDTO;
import com.smartpaymentsystem.api.dto.LoginResponseDTO;
import com.smartpaymentsystem.api.dto.RegisterRequestDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequestDTO request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        if (request.getRole() == UserRole.STAFF) {
            if (request.getBusinessId() == null) {
                throw new ConflictException("Staff user must belong to a business");
            }

            Business business = businessRepository.findById(request.getBusinessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

            user.setBusiness(business);
        } else {
            user.setBusiness(null);
        }
        return userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ConflictException("Invalid credentials"));

        boolean passwordOk = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!passwordOk) {
            throw new ConflictException("Invalid credentials");
        }

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setBusinessId(user.getBusiness() != null ? user.getBusiness().getId() : null);

        return response;
    }
}
