package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.PaymentStatus;

public interface StatusCountRow {
    PaymentStatus getStatus();
    long getCount();
}
