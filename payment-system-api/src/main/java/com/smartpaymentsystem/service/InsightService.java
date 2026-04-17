package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import com.smartpaymentsystem.api.dto.InsightResponseDTO;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    @Value("${app.insights.ai-enabled:false}")
    private boolean insightsAiEnabled;

    private final BusinessAccessService businessAccessService;
    private final AnalyticsService analyticsService;   // IMPROVEMENT: reuse shared metric builder
    private final InsightPromptBuilder insightPromptBuilder;
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    public InsightResponseDTO generateSummary(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        if (!insightsAiEnabled) {
            return buildFallbackResponse();
        }

        InsightMetricsDTO metrics = analyticsService.buildMetrics(businessId);
        String prompt = insightPromptBuilder.buildSummaryPrompt(metrics);

        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            log.warn("ChatClient is not configured; returning fallback for businessId={}", businessId);
            return buildFallbackResponse();
        }

        ChatClient chatClient = builder.build();

        try {
            InsightResponseDTO response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(InsightResponseDTO.class);

            if (response == null) {
                log.warn("AI returned null response for businessId={}", businessId);
                return buildFallbackResponse();
            }

            return response;

        } catch (Exception exception) {
            log.warn("AI insight generation failed for businessId={}: {}", businessId, exception.getMessage());
            return buildFallbackResponse();
        }
    }

    private InsightResponseDTO buildFallbackResponse() {
        InsightResponseDTO response = new InsightResponseDTO();
        response.setSummary("Unable to generate AI insights at the moment.");
        response.setRisks(List.of("AI response unavailable"));
        response.setRecommendations(List.of("Please try again later"));
        response.setConfidence("low");
        return response;
    }
}