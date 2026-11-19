package com.haichann.url_shortener.user.controller;

import com.haichann.url_shortener.common.base.ApiResponse;
import com.haichann.url_shortener.user.dto.ChangePasswordRequest;
import com.haichann.url_shortener.user.dto.UpdateMyInfoRequest;
import com.haichann.url_shortener.user.dto.ProfileDTO;
import com.haichann.url_shortener.user.service.UserService;
import com.haichann.url_shortener.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Profile", description = "Authenticated user profile and account management endpoints.")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping("me")
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile of the user identified by the JWT access token."
    )
    public ApiResponse<ProfileDTO> getMyProfile(@Parameter(hidden = true) Authentication authentication) {
        var profile = userService.getMyProfile(authentication.getName());
        return ApiResponse.<ProfileDTO>builder()
                .responseData(profile)
                .build();
    }

    @PatchMapping("me")
    @Operation(
            summary = "Update current user profile",
            description = "Updates mutable profile fields for the authenticated user."
    )
    public ApiResponse<ProfileDTO> updateMyInfo(
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody UpdateMyInfoRequest request
    ) {
        var profile = userService.updateMyInfo(authentication.getName(), request);
        return ApiResponse.<ProfileDTO>builder()
                .responseData(profile)
                .build();
    }

    @PatchMapping("me/password")
    @Operation(
            summary = "Change current user password",
            description = "Changes the password for the authenticated local account."
    )
    public ApiResponse<Void> changePassword(
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(authentication.getName(), request);
        return ApiResponse.<Void>builder()
                .message("Password changed successfully")
                .build();
    }

    @DeleteMapping("me")
    @Operation(
            summary = "Deactivate current user account",
            description = "Marks the authenticated account as inactive. The account is not permanently deleted."
    )
    public ApiResponse<Void> deactivateMyAccount(@Parameter(hidden = true) Authentication authentication) {
        userService.deactivateMyAccount(authentication.getName());
        return ApiResponse.<Void>builder()
                .message("Account deactivated successfully")
                .build();
    }

}
