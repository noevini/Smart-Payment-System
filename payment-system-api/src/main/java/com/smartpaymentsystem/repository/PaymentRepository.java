package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Payment;
import com.smartpaymentsystem.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Payment> findByBusiness_IdAndStatusNotAndDueDateBefore(
            Long businessId,
            PaymentStatus status,
            Instant now,
            Pageable pageable
    );

    @Query("""
        select p.status as status, count(p) as count
        from Payment p
        where p.business.id = :businessId
        group by p.status
    """)
    List<StatusCountRepository> countByStatus(@Param("businessId") Long businessId);

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

    @Query("""
    select
        function('date_trunc', 'month', p.paidAt) as monthStart,
        coalesce(sum(p.amount), 0) as revenue,
        count(p) as count
    from Payment p
    where p.business.id = :businessId
      and p.status = com.smartpaymentsystem.domain.PaymentStatus.PAID
      and p.paidAt >= :from
    group by function('date_trunc', 'month', p.paidAt)
    order by function('date_trunc', 'month', p.paidAt) asc
""")
    List<MonthlyRevenueRepository> getMonthlyRevenue(@Param("businessId") Long businessId,
                                              @Param("from") Instant from);
}
