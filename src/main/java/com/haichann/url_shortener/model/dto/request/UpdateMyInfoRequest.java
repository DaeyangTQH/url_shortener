package com.haichann.url_shortener.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Current user profile update request.")
public class UpdateMyInfoRequest {

    @Schema(description = "New unique username. Omit to keep the current username.", example = "haichan.dev", minLength = 3, maxLength = 50)
    @Size(min = 3, max = 50, message = "validation.username.size")
    String username;

    @Schema(description = "Avatar image URL. Omit to keep the current avatar.", example = "https://example.com/avatar.png", maxLength = 255)
    @Size(max = 255, message = "validation.max_length")
    String avatarImageUrl;
}
