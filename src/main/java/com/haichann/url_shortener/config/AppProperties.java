package com.haichann.url_shortener.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final String shortUrlPrefix;
    private final OAuth2 oauth2;

    public AppProperties(String baseUrl, OAuth2 oauth2) {
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.shortUrlPrefix = normalizedBaseUrl + "/r/";
        this.oauth2 = oauth2;
    }

    public record OAuth2(String frontendRedirectUrl) {
    }
}
