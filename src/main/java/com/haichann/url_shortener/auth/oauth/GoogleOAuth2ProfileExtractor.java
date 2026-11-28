package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.auth.mapper.AuthMapper;
import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.auth.oauth.OAuthUserProfile;
import com.haichann.url_shortener.user.enums.AuthProvider;
import com.haichann.url_shortener.auth.oauth.OAuth2ProfileExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2ProfileExtractor implements OAuth2ProfileExtractor {
    private final AuthMapper authMapper;

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public OAuthUserProfile extract(OAuth2User oauth2User) {
        Map<String, Object> attrs = oauth2User.getAttributes();
        String sub = (String) attrs.get("sub");
        String email = (String) attrs.get("email");
        boolean emailVerified = Boolean.TRUE.equals(attrs.get("email_verified"));

        if (!StringUtils.hasText(sub) || !StringUtils.hasText(email) || !emailVerified) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.oauth_profile_invalid");
        }

        return authMapper.toGoogleProfile(sub, email, (String) attrs.get("name"), (String) attrs.get("picture"));
    }
}
