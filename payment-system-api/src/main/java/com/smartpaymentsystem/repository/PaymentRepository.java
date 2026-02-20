package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBusiness_Id(Long businessId);
    Optional<Payment> findByIdAndBusiness_Id(Long paymentId, Long businessId);
    List<Payment> findByStatusAndDueDate(PaymentStatus status, Instant now);

    @Query("""
        select p.status as status, count(p) as count
        from Payment p
        where p.business.id = :businessId
        group by p.status
    """)
    List<StatusCountRow> countByStatus(@Param("businessId") Long businessId);

    @Query("""
        select coalesce(sum(p.amount), 0)
        from Payment p
        where p.business.id = :businessId
    """)
    BigDecimal sumAmountByBusinessId(@Param("businessId") Long businessId);

    @Query("""
        select coalesce(sum(p.amount), 0)
        from Payment p
        where p.business.id = :businessId
          and p.status = :status
    """)
    BigDecimal sumAmountByBusinessIdAndStatus(@Param("businessId") Long businessId,
                                              @Param("status") PaymentStatus status);
}
