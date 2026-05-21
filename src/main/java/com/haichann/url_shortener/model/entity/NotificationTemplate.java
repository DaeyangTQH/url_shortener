package com.haichann.url_shortener.model.entity;

import com.haichann.url_shortener.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "notification_template")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTemplate extends BaseEntity {
    String templateKey;
    String channel;
    String subjectTemplate;
    String templatePath;
    boolean isActive;

}
