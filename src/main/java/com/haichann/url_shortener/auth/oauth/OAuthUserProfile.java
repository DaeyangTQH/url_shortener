package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.user.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OAuthUserProfile {
    AuthProvider provider;
    String providerUserId;
    String email;
    boolean emailVerified;
    String displayName;
    String avatarUrl;
}
