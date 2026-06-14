package com.haichann.url_shortener.model.dto.response;

import com.haichann.url_shortener.model.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Current user profile response.")
public class ProfileDTO {

    @Schema(description = "User identifier.", example = "1")
    String id;

    @Schema(description = "Username.", example = "haichan")
    String username;

    @Schema(description = "Email address.", example = "haichan@example.com")
    String email;

    @Schema(description = "Whether the email has been verified.", example = "false")
    Boolean emailVerified;

    @Schema(description = "Current account status.", example = "ACTIVE")
    UserStatus status;

    @Schema(description = "Avatar image URL.", example = "https://example.com/avatar.png")
    String avatarImageUrl;

    @Schema(description = "Assigned role name.", example = "USER")
    String roleName;
}
