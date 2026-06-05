package com.haichann.url_shortener.model.entity;

import com.haichann.url_shortener.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "notification_template")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTemplate extends BaseEntity {

    @OneToMany(mappedBy = "template", orphanRemoval = true)
    List<UserNotification> userNotifications;

    String templateKey;
    String channel;
    String subjectTemplate;
    String templatePath;
    boolean isActive;
}
