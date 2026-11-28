package com.haichann.url_shortener.config;

import com.haichann.url_shortener.auth.oauth.OAuth2ProfileExtractor;
import com.haichann.url_shortener.auth.oauth.OAuth2ProviderRegistry;
import com.haichann.url_shortener.auth.oauth.OAuth2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
public class OAuth2Config {
    @Bean
    public OAuth2ProviderRegistry oauth2ProviderRegistry(
            List<OAuth2Service> services,
            List<OAuth2ProfileExtractor> extractors
    ) {
        return new OAuth2ProviderRegistry(
                services.stream().collect(Collectors.toUnmodifiableMap(OAuth2Service::getAuthProvider, Function.identity())),
                extractors.stream().collect(Collectors.toUnmodifiableMap(OAuth2ProfileExtractor::getProvider, Function.identity()))
        );
    }
}
