package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    boolean existsByOwner_IdAndName(Long ownerId, String name);
}
