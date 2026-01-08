package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBusiness_Id(Long businessId);
    Optional<Payment> findByIdAndBusiness_Id(Long paymentId, Long businessId);
}
