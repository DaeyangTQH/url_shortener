package com.haichann.url_shortener.model.entity;

import com.haichann.url_shortener.base.BaseEntity;
import com.haichann.url_shortener.model.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_identity",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_identity_provider_user", columnNames = {"provider", "provider_user_id"})
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    AuthProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    String providerUserId;

    @Column(name = "provider_email")
    String providerEmail; // received by provider - nullable

    @Column(name = "is_email_verified")
    Boolean isEmailVerified;

    @Column(name = "linked_at")
    LocalDateTime linkedAt;

    @Column(name = "is_primary")
    Boolean isPrimary;

}
