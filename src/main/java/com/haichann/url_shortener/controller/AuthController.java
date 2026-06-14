package com.haichann.url_shortener.controller;

import com.haichann.url_shortener.base.ApiResponse;
import com.haichann.url_shortener.model.dto.request.LocalRegisterRequest;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haichann.url_shortener.model.dto.request.LocalLoginRequest;

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
    
}
