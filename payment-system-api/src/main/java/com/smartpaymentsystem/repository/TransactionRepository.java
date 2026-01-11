package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Transaction;
import com.smartpaymentsystem.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByBusinessId(Long businessId, Pageable pageable);

    Optional<Transaction> findByIdAndBusinessId(Long id, Long businessId);

    Page<Transaction> findByBusinessIdAndType(Long businessId, TransactionType type, Pageable pageable);

    Page<Transaction> findByBusinessIdAndOccurredAtBetween(
            Long businessId,
            Instant start,
            Instant end,
            Pageable pageable
    );

    Page<Transaction> findByBusinessIdAndTypeAndOccurredAtBetween(
            Long businessId,
            TransactionType type,
            Instant start,
            Instant end,
            Pageable pageable
    );
}
