package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.InsightMetricsDTO;
import com.smartpaymentsystem.api.dto.InsightResponseDTO;
import com.smartpaymentsystem.security.BusinessAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    private final Environment environment;
    private final BusinessAccessService businessAccessService;
    private final AnalyticsService analyticsService;   // IMPROVEMENT: reuse shared metric builder
    private final InsightPromptBuilder insightPromptBuilder;
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    /**
     * If INSIGHTS_AI_ENABLED is unset: enable real AI when an API key is present (typical Railway setup).
     * If INSIGHTS_AI_ENABLED is true/false: that value wins (use false to force fallback even with a key).
     */
    private boolean isInsightsAiEnabled() {
        String explicit = environment.getProperty("INSIGHTS_AI_ENABLED");
        if (StringUtils.hasText(explicit)) {
            return Boolean.parseBoolean(explicit.trim());
        }
        return hasOpenAiApiKeyConfigured();
    }

    private boolean hasOpenAiApiKeyConfigured() {
        return StringUtils.hasText(environment.getProperty("OPENAI_API_KEY"))
                || StringUtils.hasText(environment.getProperty("spring.ai.openai-sdk.api-key"));
    }

    public InsightResponseDTO generateSummary(Long businessId) {
        businessAccessService.assertCanAccessBusiness(businessId);

        if (!isInsightsAiEnabled()) {
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