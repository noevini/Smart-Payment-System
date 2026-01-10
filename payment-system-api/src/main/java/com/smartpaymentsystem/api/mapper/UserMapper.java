package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.UserResponseDTO;
import com.smartpaymentsystem.domain.User;

public class UserMapper {

    public static UserResponseDTO toResponse(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
