package com.smartpaymentsystem.repository;

import java.math.BigDecimal;
import java.time.Instant;

public interface MonthlyRevenueRepository {
    Instant getMonthStart();
    BigDecimal getRevenue();
    long getCount();
}
