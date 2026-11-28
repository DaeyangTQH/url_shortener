package com.haichann.url_shortener.url.entity;

import com.haichann.url_shortener.common.base.BaseEntity;
import com.haichann.url_shortener.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_shorten")
@DynamicUpdate // Deactivation must not write back a stale click count.
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlShorten extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "short_code")
    String shortCode;
    @Column(name = "original_url")
    String originalUrl;
    @Column(name = "expiration_date")
    LocalDateTime expirationDate;
    @Column(name = "is_active")
    Boolean isActive;
    @Column(name = "password_hash")
    String passwordHash;
    @Column(name = "max_clicks")
    Integer maxClicks;
    @Column(name = "click_count")
    Integer clickCount;
    @Column(name = "last_accessed_at")
    LocalDateTime lastAccessedAt;

}
