package com.smartpaymentsystem.api.controller;

import com.smartpaymentsystem.api.dto.PredictionSummaryDTO;
import com.smartpaymentsystem.service.PredictionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesses/{businessId}/predictions")
@AllArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/summary")
    public PredictionSummaryDTO getSummary(@PathVariable Long businessId) {
        return predictionService.getSummary(businessId);
    }
}