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
import com.smartpaymentsystem.security.CurrentUserService;
import com.smartpaymentsystem.security.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final CurrentUserService currentUserService;

    public User register(RegisterRequestDTO request) {

        if (request.getRole() == null) {
            throw new ConflictException("Role is required");
        }

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

            User currentUser = currentUserService.getCurrentUser();

            if (currentUser.getRole() != UserRole.OWNER) {
                throw new ConflictException("Only owners can create staff users");
            }

            Business business = businessRepository
                    .findByIdAndOwners_Id(request.getBusinessId(), currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Business not found for this owner"));

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

        String token = jwtTokenService.generateToken(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setBusinessId(user.getBusiness() != null ? user.getBusiness().getId() : null);
        response.setToken(token);

        return response;
    }
}
