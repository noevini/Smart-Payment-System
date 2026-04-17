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
        if (raw.startsWith("jdbc:postgresql://")) {
            // jdbc:postgresql://user:pass@host:port/db is invalid for the PG driver (host becomes "user:pass@host")
            if (jdbcPostgresqlHasEmbeddedCredentials(raw)) {
                normalizeJdbcPostgresqlWithEmbeddedCredentials(raw, map);
            }
            return;
        }
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

    /**
     * True when the URL looks like {@code jdbc:postgresql://username:password@host:port/database}.
     * The PostgreSQL JDBC driver does not treat this like HTTP; it must be split into URL + datasource properties.
     */
    private static boolean jdbcPostgresqlHasEmbeddedCredentials(String raw) {
        String prefix = "jdbc:postgresql://";
        if (!raw.startsWith(prefix)) {
            return false;
        }
        String rest = raw.substring(prefix.length());
        int at = rest.lastIndexOf('@');
        if (at <= 0) {
            return false;
        }
        int slashAfterAt = rest.indexOf('/', at);
        return slashAfterAt > 0;
    }

    private static void normalizeJdbcPostgresqlWithEmbeddedCredentials(String raw, Map<String, Object> map) {
        String prefix = "jdbc:postgresql://";
        String rest = raw.substring(prefix.length());
        int at = rest.lastIndexOf('@');
        String userInfo = rest.substring(0, at);
        String hostPathAndQuery = rest.substring(at + 1);

        String username = null;
        String password = null;
        int colon = userInfo.indexOf(':');
        if (colon > 0) {
            username = urlDecode(userInfo.substring(0, colon));
            password = urlDecode(userInfo.substring(colon + 1));
        } else if (!userInfo.isEmpty()) {
            username = urlDecode(userInfo);
        }

        int q = hostPathAndQuery.indexOf('?');
        String hostPath = q >= 0 ? hostPathAndQuery.substring(0, q) : hostPathAndQuery;
        String query = q >= 0 ? hostPathAndQuery.substring(q + 1) : null;

        int slash = hostPath.indexOf('/');
        if (slash <= 0) {
            return;
        }
        String hostPort = hostPath.substring(0, slash);
        String database = hostPath.substring(slash + 1);
        if (database.isEmpty()) {
            return;
        }

        String host;
        int port;
        if (hostPort.startsWith("[")) {
            int bracketEnd = hostPort.indexOf(']');
            if (bracketEnd < 0) {
                return;
            }
            host = hostPort.substring(0, bracketEnd + 1);
            if (bracketEnd + 1 < hostPort.length() && hostPort.charAt(bracketEnd + 1) == ':') {
                port = Integer.parseInt(hostPort.substring(bracketEnd + 2));
            } else {
                port = 5432;
            }
        } else {
            int colonHp = hostPort.lastIndexOf(':');
            if (colonHp > 0) {
                host = hostPort.substring(0, colonHp);
                port = Integer.parseInt(hostPort.substring(colonHp + 1));
            } else {
                host = hostPort;
                port = 5432;
            }
        }

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
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
