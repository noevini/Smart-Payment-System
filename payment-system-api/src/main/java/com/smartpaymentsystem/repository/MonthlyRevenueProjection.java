package com.smartpaymentsystem.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MonthlyRevenueProjection {
    LocalDateTime getMonthStart();
    BigDecimal getRevenue();
    long getCount();
}