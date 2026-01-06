package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    boolean existsByOwner_IdAndName(Long ownerId, String name);
    List<Business> findByOwner_Id(Long ownerId);
    Optional<Business> findByIdAndOwner_Id(Long businessId, Long ownerId);
}
