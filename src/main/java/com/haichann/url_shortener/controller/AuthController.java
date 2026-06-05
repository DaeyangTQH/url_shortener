package com.haichann.url_shortener.controller;

import com.haichann.url_shortener.base.ApiResponse;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.service.AuthenticationService;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {

    AuthenticationService authenticationService;

    @PostMapping("login")
    public ApiResponse<AuthenticationResponse> login(@Valid @RequestBody LocalLoginRequest loginRequest) {
        var result = authenticationService.login(loginRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .responseData(result)
                .build();
    }
    
}
