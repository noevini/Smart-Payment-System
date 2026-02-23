package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.PaymentStatus;

public interface StatusCountRepository {
    PaymentStatus getStatus();
    long getCount();
}
