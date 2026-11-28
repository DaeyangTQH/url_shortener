package com.haichann.url_shortener.user.service.impl;

import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.user.mapper.UserMapper;
import com.haichann.url_shortener.user.dto.ChangePasswordRequest;
import com.haichann.url_shortener.user.dto.UpdateMyInfoRequest;
import com.haichann.url_shortener.user.dto.ProfileDTO;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.enums.UserStatus;
import com.haichann.url_shortener.user.repository.UserRepo;
import com.haichann.url_shortener.user.service.UserService;
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
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public ProfileDTO getMyProfile(String email) {
        return userMapper.toProfileDTO(getUserByEmailOrThrow(email));
    }

    @Override
    public ProfileDTO updateMyInfo(String email, UpdateMyInfoRequest request) {
        User currentUser = getUserByEmailOrThrow(email);
        String username = request.getUsername();
        if (username != null && !username.equals(currentUser.getUsername()) && userRepo.existsByUsername(username)) {
            throw new AppException(ErrorCode.CONFLICT, "error.username_exists");
        }
        userMapper.updateProfile(request, currentUser);
        return userMapper.toProfileDTO(userRepo.save(currentUser));
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User currentUser = getUserByEmailOrThrow(email);
        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(currentUser);
    }

    @Override
    public void deactivateMyAccount(String email) {
        User currentUser = getUserByEmailOrThrow(email);
        currentUser.setStatus(UserStatus.INACTIVE);
        userRepo.save(currentUser);
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepo.getUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.user_not_found"));
    }
}
