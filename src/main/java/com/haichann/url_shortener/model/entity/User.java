package com.haichann.url_shortener.model.entity;

import com.haichann.url_shortener.base.BaseEntity;
import com.haichann.url_shortener.model.enums.UserStatus;
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
