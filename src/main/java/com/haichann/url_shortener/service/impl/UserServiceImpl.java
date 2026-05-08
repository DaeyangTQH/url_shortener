package com.haichann.url_shortener.service.impl;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import com.haichann.url_shortener.model.dto.response.ProfileDTO;
import com.haichann.url_shortener.model.entity.User;
import com.haichann.url_shortener.model.enums.UserStatus;
import com.haichann.url_shortener.repository.UserRepo;
import com.haichann.url_shortener.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepo userRepo;
    PasswordEncoder passwordEncoder;

    @Override
    public ProfileDTO getMyProfile(String email) {
        User user = getUserByEmailOrThrow(email);
        return ProfileDTO.builder()
                .id(String.valueOf(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .status(user.getStatus())
                .avatarImageUrl(user.getAvatarImageUrl())
                .roleName(user.getRole() == null ? null : user.getRole().getRoleName())
                .build();
    }

    @Override
    public User updateMyInfo(String email, User user) {
        User currentUser = getUserByEmailOrThrow(email);
        if (user.getUsername() != null) {
            currentUser.setUsername(user.getUsername());
        }
        if (user.getAvatarImageUrl() != null) {
            currentUser.setAvatarImageUrl(user.getAvatarImageUrl());
        }
        return userRepo.save(currentUser);
    }

    @Override
    public User changePassword(String email, String newPasswordHash) {
        User currentUser = getUserByEmailOrThrow(email);
        currentUser.setPasswordHash(passwordEncoder.encode(newPasswordHash));
        return userRepo.save(currentUser);
    }

    @Override
    public User deactivateMyAccount(String email) {
        User currentUser = getUserByEmailOrThrow(email);
        currentUser.setStatus(UserStatus.INACTIVE);
        return userRepo.save(currentUser);
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepo.getUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }
}
