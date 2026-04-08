package com.smartpaymentsystem.domain;

public enum NotificationType {
    PAYMENT_OVERDUE,
    PAYMENT_PAID,
    PAYMENT_DUE_SOON  // For future scheduler: warn X days before due date
}