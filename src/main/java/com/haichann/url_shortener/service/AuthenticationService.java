package com.haichann.url_shortener.service;

import com.haichann.url_shortener.model.dto.request.LocalLoginRequest;
import com.haichann.url_shortener.model.dto.request.LocalRegisterRequest;
import com.haichann.url_shortener.model.dto.request.LogoutRequest;
import com.haichann.url_shortener.model.dto.request.RefreshTokenRequest;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(LocalLoginRequest request);
    AuthenticationResponse register(LocalRegisterRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken);
    void logout(String authorizationHeader, LogoutRequest request);

}
