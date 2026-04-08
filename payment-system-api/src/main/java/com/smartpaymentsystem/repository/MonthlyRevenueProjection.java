package com.smartpaymentsystem.repository;

import java.math.BigDecimal;
import java.time.Instant;

public interface MonthlyRevenueProjection {
    Instant getMonthStart();
    BigDecimal getRevenue();
    long getCount();
}