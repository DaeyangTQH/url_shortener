package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.user.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.StringUtils;

import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2ProviderRegistry {

    Map<AuthProvider, OAuth2Service> services;
    Map<AuthProvider, OAuth2ProfileExtractor> extractors;

    // Registration id in application.yaml must match the AuthProvider enum name
    public AuthProvider resolveProvider(String registrationId) {
        if (!StringUtils.hasText(registrationId)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.oauth_provider_unsupported");
        }

        try {
            return AuthProvider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.oauth_provider_unsupported");
        }
    }

    public OAuth2Service getService(AuthProvider provider) {
        OAuth2Service service = services.get(provider);
        if (service == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.oauth_provider_unsupported");
        }
        return service;
    }

    public OAuth2ProfileExtractor getExtractor(AuthProvider provider) {
        OAuth2ProfileExtractor extractor = extractors.get(provider);
        if (extractor == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.oauth_provider_unsupported");
        }
        return extractor;
    }
}
