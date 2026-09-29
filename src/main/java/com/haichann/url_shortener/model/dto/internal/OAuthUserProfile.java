package com.haichann.url_shortener.model.dto.internal;

import com.haichann.url_shortener.model.enums.AuthProvider;
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
