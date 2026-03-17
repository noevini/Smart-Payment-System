package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import com.smartpaymentsystem.api.dto.InsightResponseDTO;
import com.smartpaymentsystem.repository.PaymentRepository;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InsightService {
    private final BusinessAccessService businessAccessService;
    private final PaymentRepository paymentRepository;

    public InsightResponseDTO generateSummary(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        InsightMetricsDTO metrics = buildMetrics(businessId);

        InsightResponseDTO response = new InsightResponseDTO();
        response.setSummary("Temporary summary");
        response.setRisks(List.of("Temporary risk"));
        response.setRecommendations(List.of("Temporary recommendation"));
        response.setConfidence("low");

        return response;
    }

    private InsightMetricsDTO buildMetrics(Long businessId) {
        return new InsightMetricsDTO();
    }
}
