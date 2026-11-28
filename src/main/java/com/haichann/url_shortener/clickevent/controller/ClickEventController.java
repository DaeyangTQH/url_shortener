package com.haichann.url_shortener.clickevent.controller;

import com.haichann.url_shortener.clickevent.dto.DailyClickCount;
import com.haichann.url_shortener.clickevent.service.ClickEventService;
import com.haichann.url_shortener.common.base.ApiResponse;
import com.haichann.url_shortener.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Click Analytics")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class ClickEventController {
    private final ClickEventService clickEventService;

    @GetMapping("/api/v1/urls/{id}/stats")
    @Operation(
            summary = "Get daily clicks for an owned short link",
            description = "Returns recorded clicks for 1–30 calendar days, including today and days with zero clicks. "
                    + "Historical totals from before click events were recorded are not included. "
                    + "Returns 404 for links that are missing, deleted, or owned by another user."
    )
    public ApiResponse<List<DailyClickCount>> getDailyStats(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long id,
            @RequestParam(defaultValue = "7") int days
    ) {
        return ApiResponse.<List<DailyClickCount>>builder()
                .responseData(clickEventService.getDailyStats(authentication.getName(), id, days))
                .build();
    }
}
