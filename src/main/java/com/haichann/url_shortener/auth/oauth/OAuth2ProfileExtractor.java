package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.auth.oauth.OAuthUserProfile;
import com.haichann.url_shortener.user.enums.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2ProfileExtractor {
    AuthProvider getProvider();
    OAuthUserProfile extract(OAuth2User oauth2User);
}
