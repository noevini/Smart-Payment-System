package com.smartpaymentsystem.api.repository;

import com.smartpaymentsystem.api.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {
}
