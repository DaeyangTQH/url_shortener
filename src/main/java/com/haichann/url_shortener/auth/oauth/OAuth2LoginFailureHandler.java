package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    AppProperties appProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(appProperties.getOauth2().frontendRedirectUrl() + "/oauth/callback")
                .queryParam("error", "oauth_failed")
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
