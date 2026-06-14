package com.haichann.url_shortener.service.impl;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import com.haichann.url_shortener.helper.JwtHelper;
import com.haichann.url_shortener.model.dto.request.LocalLoginRequest;
import com.haichann.url_shortener.model.dto.request.LocalRegisterRequest;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.model.entity.User;
import com.haichann.url_shortener.model.enums.UserStatus;
import com.haichann.url_shortener.repository.RoleRepo;
import com.haichann.url_shortener.repository.UserRepo;
import com.haichann.url_shortener.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepo userRepo;
    RoleRepo roleRepo;
    JwtHelper jwtHelper;
    PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponse login(LocalLoginRequest request) {
        User user = userRepo.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (UserStatus.BANNED.equals(user.getStatus())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        String token = jwtHelper.generateToken(user);
        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(token)
                .build();
    }

    @Override
    public AuthenticationResponse register(LocalRegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.CONFLICT, "error.email_exists");
        }

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.CONFLICT, "error.username_exists");
        }

        var userRole = roleRepo.findByRoleName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR, "error.default_user_role_missing"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .avatarImageUrl("")
                .build();

        User savedUser = userRepo.save(user);
        String token = jwtHelper.generateToken(savedUser);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(token)
                .build();
    }
}
