package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.PaymentStatus;

public interface StatusCountProjection {
    PaymentStatus getStatus();
    long getCount();
}