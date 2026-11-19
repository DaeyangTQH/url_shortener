package com.haichann.url_shortener.auth.service;

import com.haichann.url_shortener.auth.dto.LocalLoginRequest;
import com.haichann.url_shortener.auth.dto.LocalRegisterRequest;
import com.haichann.url_shortener.auth.dto.LogoutRequest;
import com.haichann.url_shortener.auth.dto.RefreshTokenRequest;
import com.haichann.url_shortener.auth.dto.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(LocalLoginRequest request);
    AuthenticationResponse register(LocalRegisterRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken);
    void logout(String authorizationHeader, LogoutRequest request);

}
