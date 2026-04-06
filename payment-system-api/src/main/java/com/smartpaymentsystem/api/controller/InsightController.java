package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.InsightResponseDTO;
import com.smartpaymentsystem.service.InsightService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesses/{businessId}/insights")
@AllArgsConstructor
public class InsightController {
    private final InsightService insightService;

    @GetMapping("/summary")
    public InsightResponseDTO getSummary(@PathVariable Long businessId) {
        return insightService.generateSummary(businessId);
    }
}
