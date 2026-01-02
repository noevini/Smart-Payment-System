package com.smartpaymentsystem.api.service;

import com.smartpaymentsystem.api.domain.User;
import com.smartpaymentsystem.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow();
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
