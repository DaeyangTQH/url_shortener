package com.haichann.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Shortened URL details.")
public class UrlResponse {

    @Schema(description = "URL record id.", example = "1")
    Long id;

    @Schema(description = "Generated short code.", example = "aB3xY9k")
    String shortCode;

    @Schema(description = "Full short URL ready to share.", example = "http://localhost:8080/r/aB3xY9k")
    String shortUrl;

    @Schema(description = "Original long URL.", example = "https://example.com/very/long/path")
    String originalUrl;

    @Schema(description = "Expiration timestamp.")
    LocalDateTime expirationDate;

    @Schema(description = "Whether the short link is active.", example = "true")
    Boolean isActive;

    @Schema(description = "Number of redirects so far.", example = "0")
    Integer clickCount;

    @Schema(description = "Creation timestamp.")
    LocalDateTime createdAt;
}
