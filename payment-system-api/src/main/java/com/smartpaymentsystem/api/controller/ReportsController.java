package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.DashboardResponseDTO;
import com.smartpaymentsystem.api.dto.MonthlyRevenueResponseDTO;
import com.smartpaymentsystem.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@AllArgsConstructor
public class ReportsController {
    private final ReportService reportService;

    @GetMapping("/dashboard")
    public DashboardResponseDTO getDashboardSummary() {
        return reportService.getDashboardSummary();
    }

    @GetMapping("/monthly-revenue")
    public MonthlyRevenueResponseDTO getMonthlyRevenue(@RequestParam(defaultValue = "6") int months) {
        return reportService.getMonthlyRevenue(months);
    }
}
