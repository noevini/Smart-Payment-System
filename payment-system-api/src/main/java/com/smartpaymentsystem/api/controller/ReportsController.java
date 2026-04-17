package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.DashboardResponseDTO;
import com.smartpaymentsystem.api.dto.MonthlyRevenueResponseDTO;
import com.smartpaymentsystem.api.dto.OverduePaymentResponseDTO;
import com.smartpaymentsystem.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesses/{businessId}/reports")
@AllArgsConstructor
public class ReportsController {
    private final ReportService reportService;

    @GetMapping("/dashboard")
    public DashboardResponseDTO getDashboardSummary(@PathVariable Long businessId) {
        return reportService.getDashboardSummary(businessId);
    }

    @GetMapping("/monthly-revenue")
    public MonthlyRevenueResponseDTO getMonthlyRevenue(@PathVariable Long businessId,
                                                       @RequestParam(defaultValue = "6") int months) {
        return reportService.getMonthlyRevenue(businessId, months);
    }

    @GetMapping("/overdue")
    public OverduePaymentResponseDTO getOverduePayments(@PathVariable Long businessId,
                                                        @RequestParam(defaultValue = "20") int limit) {
        return reportService.getOverduePayments(businessId, limit);
    }
}
