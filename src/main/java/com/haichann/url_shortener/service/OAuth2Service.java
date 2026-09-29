package com.haichann.url_shortener.service;

import com.haichann.url_shortener.model.dto.internal.OAuthUserProfile;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.model.enums.AuthProvider;

public interface OAuth2Service {
    AuthProvider getAuthProvider();
    AuthenticationResponse processLogin(OAuthUserProfile profile);
}