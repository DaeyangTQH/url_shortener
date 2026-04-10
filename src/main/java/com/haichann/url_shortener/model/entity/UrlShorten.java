package com.haichann.url_shortener.model.entity;

import com.haichann.url_shortener.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_shorten")
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
    @Column(name = "last_access")
    LocalDateTime lastAccess;

    @Column(name = "created_ip")
    String createdIP;
    @Column(name = "last_access_ip")
    String lastAccessIP;


}
