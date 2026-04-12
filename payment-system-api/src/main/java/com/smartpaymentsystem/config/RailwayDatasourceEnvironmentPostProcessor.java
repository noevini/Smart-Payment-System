package com.smartpaymentsystem.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class RailwayDatasourceEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String SOURCE_NAME = "deploymentEnvironmentDefaults";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> map = new LinkedHashMap<>();
        applyPostgresUrlFromRailway(environment, map);
        disableOpenAiIfNoApiKey(environment, map);
        if (!map.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(SOURCE_NAME, map));
        }
    }

    private static void applyPostgresUrlFromRailway(ConfigurableEnvironment environment, Map<String, Object> map) {
        String raw = environment.getProperty("DATABASE_URL");
        if (raw == null || raw.isBlank()) {
            return;
        }
        raw = raw.trim();
        if (raw.startsWith("jdbc:")) {
            return;
        }
        if (!raw.startsWith("postgresql://") && !raw.startsWith("postgres://")) {
            return;
        }

        URI uri = URI.create(raw);
        String userInfo = uri.getUserInfo();
        String username = null;
        String password = null;
        if (userInfo != null && !userInfo.isEmpty()) {
            int colon = userInfo.indexOf(':');
            if (colon >= 0) {
                username = urlDecode(userInfo.substring(0, colon));
                password = urlDecode(userInfo.substring(colon + 1));
            } else {
                username = urlDecode(userInfo);
            }
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            return;
        }
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getPath();
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path == null || path.isEmpty()) {
            return;
        }

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + path;
        String query = uri.getRawQuery();
        if (query != null && !query.isEmpty()) {
            jdbcUrl += "?" + query;
            if (!containsSslMode(query)) {
                jdbcUrl += "&sslmode=require";
            }
        } else {
            jdbcUrl += "?sslmode=require";
        }

        map.put("spring.datasource.url", jdbcUrl);
        if (username != null) {
            map.put("spring.datasource.username", username);
        }
        if (password != null) {
            map.put("spring.datasource.password", password);
        }
    }

    private static void disableOpenAiIfNoApiKey(ConfigurableEnvironment environment, Map<String, Object> map) {
        if (hasOpenAiApiKey(environment)) {
            return;
        }
        if (StringUtils.hasText(environment.getProperty("spring.ai.model.chat"))) {
            return;
        }
        map.put("spring.ai.model.chat", "none");
    }

    private static boolean hasOpenAiApiKey(ConfigurableEnvironment environment) {
        return StringUtils.hasText(environment.getProperty("OPENAI_API_KEY"))
                || StringUtils.hasText(environment.getProperty("spring.ai.openai.api-key"))
                || StringUtils.hasText(environment.getProperty("spring.ai.openai-sdk.api-key"));
    }

    private static boolean containsSslMode(String query) {
        return query.contains("sslmode=");
    }

    private static String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
