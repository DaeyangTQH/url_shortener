package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.auth.oauth.OAuthUserProfile;
import com.haichann.url_shortener.auth.dto.AuthenticationResponse;
import com.haichann.url_shortener.config.AppProperties;
import com.haichann.url_shortener.user.enums.AuthProvider;
import com.haichann.url_shortener.auth.oauth.OAuth2ProviderRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    AppProperties appProperties;

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
                    .fromUriString(appProperties.getOauth2().frontendRedirectUrl() + CALLBACK_PATH)
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
                .fromUriString(appProperties.getOauth2().frontendRedirectUrl() + CALLBACK_PATH)
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
