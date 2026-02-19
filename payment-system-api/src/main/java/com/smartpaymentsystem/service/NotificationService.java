package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.NotificationResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.NotificationMapper;
import com.smartpaymentsystem.domain.Notification;
import com.smartpaymentsystem.repository.NotificationRepository;
import com.smartpaymentsystem.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public Page<NotificationResponseDTO> listNotifications(Pageable pageable) {
        Long businessId = currentUserService.getCurrentUser().getBusiness().getId();
        return  notificationRepository.findByBusiness_id(businessId, pageable).map(notificationMapper::toResponseDTO);
    }

    public void markAsRead(Long notificationId) {
        Long businessId = currentUserService.getCurrentUser().getBusiness().getId();

        Notification notification = notificationRepository.findByIdAndBusiness_Id(notificationId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
}
