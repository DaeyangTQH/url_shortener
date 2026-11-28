package com.haichann.url_shortener.url.controller;

import com.haichann.url_shortener.common.base.ApiResponse;
import com.haichann.url_shortener.common.base.PageResponse;
import com.haichann.url_shortener.config.OpenApiConfig;
import com.haichann.url_shortener.url.dto.CreateUrlRequest;
import com.haichann.url_shortener.url.dto.UrlResponse;
import com.haichann.url_shortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
@Tag(name = "URL Shortener", description = "Create and manage shortened URLs for the authenticated user.")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UrlController {

    UrlService urlService;

    @PostMapping
    @Operation(
            summary = "Create a shortened URL",
            description = "Creates a short link for the given original URL. Requires authentication."
    )
    public ApiResponse<UrlResponse> create(
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody CreateUrlRequest request
    ) {
        UrlResponse response = urlService.create(authentication.getName(), request);
        return ApiResponse.<UrlResponse>builder()
                .message("Short URL created successfully")
                .responseData(response)
                .build();
    }

    @GetMapping
    @Operation(
            summary = "List my shortened URLs",
            description = "Returns a paginated list of non-deleted short links owned by the authenticated user. "
                    + "Use page, size, and sort query params (e.g. sort=createdAt,desc)."
    )
    public ApiResponse<PageResponse<UrlResponse>> listMine(
            @Parameter(hidden = true) Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<UrlResponse>>builder()
                .responseData(urlService.listMine(authentication.getName(), pageable))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a shortened URL by id",
            description = "Returns details for a short link owned by the authenticated user."
                    + " Returns 404 if the link does not exist, was deleted, or is not owned by the user."
    )
    public ApiResponse<UrlResponse> getById(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long id
    ) {
        return ApiResponse.<UrlResponse>builder()
                .responseData(urlService.getById(authentication.getName(), id))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deactivate a shortened URL",
            description = "Soft-deletes and deactivates a short link owned by the authenticated user."
                    + " Returns 404 if the link does not exist, was deleted, or is not owned by the user."
    )
    public ApiResponse<Void> deactivate(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long id
    ) {
        urlService.deactivate(authentication.getName(), id);
        return ApiResponse.<Void>builder()
                .message("Short URL deactivated successfully")
                .build();
    }
}
