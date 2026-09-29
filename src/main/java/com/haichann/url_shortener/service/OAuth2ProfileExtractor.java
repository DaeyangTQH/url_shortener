package com.haichann.url_shortener.service;

import com.haichann.url_shortener.model.dto.internal.OAuthUserProfile;
import com.haichann.url_shortener.model.enums.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2ProfileExtractor {
    AuthProvider getProvider();
    OAuthUserProfile extract(OAuth2User oauth2User);
}
