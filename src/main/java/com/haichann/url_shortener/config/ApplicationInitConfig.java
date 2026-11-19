package com.haichann.url_shortener.config;

import com.haichann.url_shortener.user.entity.Role;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.enums.UserStatus;
import com.haichann.url_shortener.user.repository.RoleRepo;
import com.haichann.url_shortener.user.repository.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepo userRepo, RoleRepo roleRepo) {
        return args -> {
            Role adminRole = roleRepo.findByRoleName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setRoleName("ADMIN");
                return roleRepo.save(role);
            });

            Role userRole = roleRepo.findByRoleName("USER").orElseGet(() -> {
                Role role = new Role();
                role.setRoleName("USER");
                return roleRepo.save(role);
            });

            if (userRepo.getUserByEmail("admin@admin.com").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@admin.com")
                        .avatarImageUrl("https://cloudinary-marketing-res.cloudinary.com/image/upload/w_1300/q_auto/f_auto/hiking_dog_mountain")
                        .passwordHash(passwordEncoder.encode("Admin@123"))
                        .role(adminRole)
                        .status(UserStatus.ACTIVE)
                        .emailVerified(true)
                        .build();
                userRepo.save(admin);
                log.warn("Admin user created — email: admin@admin.com / password: Admin@123");
            }
        };
    }
}
