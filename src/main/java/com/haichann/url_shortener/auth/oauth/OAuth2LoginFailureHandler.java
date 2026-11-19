package com.haichann.url_shortener.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @NonFinal
    @Value("${app.oauth2.frontend-redirect-url}")
    String frontendRedirectUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUrl + "/oauth/callback")
                .queryParam("error", "oauth_failed")
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
