package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Notification;
import com.smartpaymentsystem.domain.NotificationType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByBusiness_id(Long businessId, Pageable pageable);

    Optional<Notification> findByIdAndBusiness_Id(Long id, Long businessId);

    boolean existsByBusiness_IdAndTypeAndRelatedEntityTypeAndRelatedEntityId(
            Long business_id, @NotNull NotificationType type, String relatedEntityType, Long relatedEntityId
    );
}
