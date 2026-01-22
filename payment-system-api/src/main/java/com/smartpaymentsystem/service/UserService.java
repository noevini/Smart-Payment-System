package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User updateUser(Long userId, String name, String phone) {
        User user = getById(userId);

        boolean changed = false;

        if (name != null) {
            String requestedName = name.trim();
            if (!user.getName().trim().equals(requestedName)) {
                user.setName(requestedName);
                changed = true;
            }
        }

        if (phone != null) {
            String requestedPhone = phone.trim();
            if (!java.util.Objects.equals(user.getPhone(), requestedPhone)) {
                user.setPhone(requestedPhone);
                changed = true;
            }
        }

        if (!changed) {
            return user;
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = getById(userId);
        userRepository.delete(user);
    }
}
