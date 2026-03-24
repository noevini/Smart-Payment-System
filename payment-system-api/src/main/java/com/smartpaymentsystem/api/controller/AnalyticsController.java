package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.AnalyticsSummaryDTO;
import com.smartpaymentsystem.service.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesses/{businessId}/analytics")
@AllArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public AnalyticsSummaryDTO getSummary(@PathVariable Long businessId) {
        return analyticsService.getSummary(businessId);
    }
}