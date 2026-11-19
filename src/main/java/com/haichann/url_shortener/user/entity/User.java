package com.haichann.url_shortener.user.entity;

import com.haichann.url_shortener.common.base.BaseEntity;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    List<UrlShorten> urlShortenList;

    String username;
    String passwordHash;
    String email;
    Boolean emailVerified;
    @Enumerated(EnumType.STRING)
    UserStatus status;
    LocalDateTime lastLoginAt;
    String avatarImageUrl;

}
