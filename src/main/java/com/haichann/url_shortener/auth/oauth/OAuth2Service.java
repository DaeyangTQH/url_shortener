package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.auth.oauth.OAuthUserProfile;
import com.haichann.url_shortener.auth.dto.AuthenticationResponse;
import com.haichann.url_shortener.user.enums.AuthProvider;

public interface OAuth2Service {
    AuthProvider getAuthProvider();
    AuthenticationResponse processLogin(OAuthUserProfile profile);
}