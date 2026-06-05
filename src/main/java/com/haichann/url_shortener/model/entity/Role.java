package com.haichann.url_shortener.model.entity;

import com.haichann.url_shortener.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

    @OneToMany(mappedBy = "role")
    List<User> users;   

    @Column(name = "role_name")
    String roleName;
}
