package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.NotificationResponseDTO;
import com.smartpaymentsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public Page<NotificationResponseDTO> listNotifications(Pageable pageable) {
        return notificationService.listNotifications(pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/read)")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
