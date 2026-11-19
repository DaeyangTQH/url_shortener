package com.haichann.url_shortener.url.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_event")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClickEvent {

    @ManyToOne @JoinColumn(name = "url_id")
    UrlShorten urlShorten;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    String userAgent;

    @Column(name = "referer", columnDefinition = "TEXT")
    String referer;

    @Column(name = "country", length = 10)
    String country;

    @Column(name = "device_type", length = 20)
    String deviceType;

    @Column(name = "browser", length = 50)
    String browser;

    @Column(name = "os", length = 50)
    String os;

    @Column(name = "clicked_at", nullable = false)
    LocalDateTime clickedAt;

    @PrePersist
    public void prePersist() {
        this.clickedAt = LocalDateTime.now();
    }
}