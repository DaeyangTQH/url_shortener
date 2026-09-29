package com.haichann.url_shortener.config;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.model.dto.internal.OAuthUserProfile;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.model.enums.AuthProvider;
import com.haichann.url_shortener.service.OAuth2ProviderRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    static final String CALLBACK_PATH = "/oauth/callback";

    OAuth2ProviderRegistry oauth2ProviderRegistry;

    @NonFinal
    @Value("${app.oauth2.frontend-redirect-url}")
    String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            AuthProvider provider =
                    oauth2ProviderRegistry.resolveProvider(token.getAuthorizedClientRegistrationId());

            OAuthUserProfile profile =
                    oauth2ProviderRegistry.getExtractor(provider).extract(token.getPrincipal());
            AuthenticationResponse authResponse =
                    oauth2ProviderRegistry.getService(provider).processLogin(profile);

            // The OAuth handshake session is no longer needed once the JWT is issued
            invalidateSession(request);

            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendRedirectUrl + CALLBACK_PATH)
                    .queryParam("accessToken", authResponse.getAccessToken())
                    .queryParam("refreshToken", authResponse.getRefreshToken())
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
        } catch (AppException exception) {
            redirectWithError(request, response, exception.getMessage());
        } catch (Exception exception) {
            redirectWithError(request, response, "oauth_failed");
        }
    }

    private void redirectWithError(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String error) throws IOException {
        invalidateSession(request);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUrl + CALLBACK_PATH)
                .queryParam("error", error)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
