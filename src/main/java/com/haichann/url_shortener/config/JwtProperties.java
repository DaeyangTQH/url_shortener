package com.haichann.url_shortener.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String secretKey;
    private final String issuer;
    private final long expirationMinutes;
    private final long refreshExpirationDays;
}
