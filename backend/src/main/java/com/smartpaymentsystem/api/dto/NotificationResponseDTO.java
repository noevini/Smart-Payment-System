package com.smartpaymentsystem.api.dto;

import com.smartpaymentsystem.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean isRead;
    private Instant createdAt;
    private String relatedEntityType;
    private Long relatedEntityId;
}
