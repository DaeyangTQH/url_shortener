package com.haichann.url_shortener.auth.controller;

import com.haichann.url_shortener.common.base.ApiResponse;
import com.haichann.url_shortener.auth.dto.LocalRegisterRequest;
import com.haichann.url_shortener.auth.dto.LogoutRequest;
import com.haichann.url_shortener.auth.dto.RefreshTokenRequest;
import com.haichann.url_shortener.auth.dto.AuthenticationResponse;
import com.haichann.url_shortener.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.haichann.url_shortener.auth.dto.LocalLoginRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Local authentication endpoints for issuing JWT access tokens.")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            summary = "Login with email and password",
            description = "Authenticates a local user and returns a JWT access token."
    )
    public ApiResponse<AuthenticationResponse> login(@Valid @RequestBody LocalLoginRequest loginRequest) {
        var result = authenticationService.login(loginRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .responseData(result)
                .build();
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a local account",
            description = "Creates a new user account with the default USER role and returns a JWT access token."
    )
    public ApiResponse<AuthenticationResponse> register(@Valid @RequestBody LocalRegisterRequest registerRequest) {
        var result = authenticationService.register(registerRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .responseData(result)
                .build();
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Refreshes the JWT access token using a valid refresh token."
    )
    public ApiResponse<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        var result = authenticationService.refreshToken(refreshToken);
        return ApiResponse.<AuthenticationResponse>builder()
                .responseData(result)
                .build();
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout and invalidate refresh token",
            description = "Logs out the user and invalidates the provided refresh token."
    )
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody LogoutRequest request
    ) {
        authenticationService.logout(authorizationHeader, request);
        return ApiResponse.<Void>builder().build();
    }
}
