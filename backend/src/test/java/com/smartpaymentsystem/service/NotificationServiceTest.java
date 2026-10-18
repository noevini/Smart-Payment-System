package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.NotificationResponseDTO;
import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.api.mapper.NotificationMapper;
import com.smartpaymentsystem.domain.*;
import com.smartpaymentsystem.repository.NotificationRepository;
import com.smartpaymentsystem.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationMapper notificationMapper;
    @Mock private NotificationRepository notificationRepository;
    @Mock private CurrentUserService currentUserService;

    @InjectMocks
    private NotificationService notificationService;

    private Business business;
    private Notification notification;
    private NotificationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(1L);
        business.setName("Test Business");

        notification = new Notification();
        notification.setId(10L);
        notification.setBusiness(business);
        notification.setType(NotificationType.PAYMENT_OVERDUE);
        notification.setTitle("Payment overdue");
        notification.setMessage("You have an overdue payment.");
        notification.setRead(false);

        responseDTO = new NotificationResponseDTO(
                10L, NotificationType.PAYMENT_OVERDUE,
                "Payment overdue", "You have an overdue payment.",
                false, Instant.now(), "PAYMENT", 1L
        );
    }

    // ── listNotifications ──

    @Test
    void listNotifications_returnsPageOfDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(List.of(notification));

        when(currentUserService.getCurrentBusinessId()).thenReturn(1L);
        when(notificationRepository.findByBusiness_Id(1L, pageable)).thenReturn(page);
        when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

        Page<NotificationResponseDTO> result = notificationService.listNotifications(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(10L, result.getContent().get(0).getId());
    }

    @Test
    void listNotifications_emptyPage_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(currentUserService.getCurrentBusinessId()).thenReturn(1L);
        when(notificationRepository.findByBusiness_Id(1L, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<NotificationResponseDTO> result = notificationService.listNotifications(pageable);

        assertEquals(0, result.getTotalElements());
    }

    // ── markAsRead ──

    @Test
    void markAsRead_unreadNotification_setsReadAndSaves() {
        notification.setRead(false);
        when(currentUserService.getCurrentBusinessId()).thenReturn(1L);
        when(notificationRepository.findByIdAndBusiness_Id(10L, 1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_alreadyRead_doesNotSaveAgain() {
        notification.setRead(true);
        when(currentUserService.getCurrentBusinessId()).thenReturn(1L);
        when(notificationRepository.findByIdAndBusiness_Id(10L, 1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_notFound_throwsResourceNotFoundException() {
        when(currentUserService.getCurrentBusinessId()).thenReturn(1L);
        when(notificationRepository.findByIdAndBusiness_Id(99L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markAsRead(99L));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_usesBusinessIdFromHeader() {
        when(currentUserService.getCurrentBusinessId()).thenReturn(1L);
        when(notificationRepository.findByIdAndBusiness_Id(10L, 1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        verify(currentUserService).getCurrentBusinessId();
        verify(notificationRepository).findByIdAndBusiness_Id(10L, 1L);
    }
}
