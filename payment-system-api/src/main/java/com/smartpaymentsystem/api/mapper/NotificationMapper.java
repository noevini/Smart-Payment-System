package com.smartpaymentsystem.api.mapper;

import com.smartpaymentsystem.api.dto.NotificationResponseDTO;
import com.smartpaymentsystem.domain.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getRelatedEntityType(),
                notification.getRelatedEntityId()
        );
    }
}
