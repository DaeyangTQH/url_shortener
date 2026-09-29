package com.haichann.url_shortener.service;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import com.haichann.url_shortener.model.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2ProviderRegistry {

    Map<AuthProvider, OAuth2Service> services;
    Map<AuthProvider, OAuth2ProfileExtractor> extractors;

    public OAuth2ProviderRegistry(List<OAuth2Service> oauth2Services,
                                  List<OAuth2ProfileExtractor> oauth2ProfileExtractors) {
        this.services = oauth2Services.stream()
                .collect(Collectors.toUnmodifiableMap(OAuth2Service::getAuthProvider, Function.identity()));
        this.extractors = oauth2ProfileExtractors.stream()
                .collect(Collectors.toUnmodifiableMap(OAuth2ProfileExtractor::getProvider, Function.identity()));
    }

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
