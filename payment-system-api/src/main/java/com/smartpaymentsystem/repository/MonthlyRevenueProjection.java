package com.smartpaymentsystem.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface MonthlyRevenueProjection {
    Timestamp getMonthStart();
    BigDecimal getRevenue();
    long getCount();
}