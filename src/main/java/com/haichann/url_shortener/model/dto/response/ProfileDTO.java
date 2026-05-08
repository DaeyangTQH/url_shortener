package com.haichann.url_shortener.model.dto.response;

import com.haichann.url_shortener.model.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {
    String id;
    String username;
    String email;
    Boolean emailVerified;
    UserStatus status;
    String avatarImageUrl;
    String roleName;
}
