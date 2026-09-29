package com.haichann.url_shortener.service.impl;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import com.haichann.url_shortener.helper.JwtHelper;
import com.haichann.url_shortener.model.dto.request.LocalLoginRequest;
import com.haichann.url_shortener.model.dto.request.LocalRegisterRequest;
import com.haichann.url_shortener.model.dto.request.LogoutRequest;
import com.haichann.url_shortener.model.dto.request.RefreshTokenRequest;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.model.entity.RefreshToken;
import com.haichann.url_shortener.model.entity.User;
import com.haichann.url_shortener.model.enums.UserStatus;
import com.haichann.url_shortener.repository.RefreshTokenRepo;
import com.haichann.url_shortener.repository.RoleRepo;
import com.haichann.url_shortener.repository.UserRepo;
import com.haichann.url_shortener.service.AccessTokenBlacklistService;
import com.haichann.url_shortener.service.AuthenticationService;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    static final String BEARER_PREFIX = "Bearer ";

    UserRepo userRepo;
    RoleRepo roleRepo;
    JwtHelper jwtHelper;
    PasswordEncoder passwordEncoder;
    RefreshTokenRepo refreshTokenRepo;
    AccessTokenBlacklistService accessTokenBlacklistService;

    @NonFinal
    @Value("${jwt.refresh-expiration-days}")
    long refreshExpirationDays;

    @Transactional
    @Override
    public AuthenticationResponse login(LocalLoginRequest request) {
        User user = userRepo.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (user.getPasswordHash() == null) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "error.use_oauth_login");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (UserStatus.BANNED.equals(user.getStatus())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        String accessToken = jwtHelper.generateToken(user);
        String refreshToken = jwtHelper.generateRefreshToken();

        setRefreshToken(refreshToken, user);
        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
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
        String accessToken = jwtHelper.generateToken(savedUser);
        String refreshToken = jwtHelper.generateRefreshToken();

        setRefreshToken(refreshToken, savedUser);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();
        String oldRefreshTokenHash = hashToken(oldRefreshToken);

        RefreshToken oldRefreshTokenEntity = refreshTokenRepo.findByTokenHash(oldRefreshTokenHash)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (oldRefreshTokenEntity.getRevokedAt() != null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        if (oldRefreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = oldRefreshTokenEntity.getUser();

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }

        String newAccessToken = jwtHelper.generateToken(user);
        String newRefreshToken = jwtHelper.generateRefreshToken();
        String newRefreshTokenHash = hashToken(newRefreshToken);

        oldRefreshTokenEntity.setRevokedAt(LocalDateTime.now());
        oldRefreshTokenEntity.setReplacedByTokenHash(newRefreshTokenHash);

        RefreshToken newRefreshTokenEntity = buildRefreshToken(newRefreshTokenHash, user);

        refreshTokenRepo.save(oldRefreshTokenEntity);
        refreshTokenRepo.save(newRefreshTokenEntity);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    @Override
    public void logout(String authorizationHeader, LogoutRequest request) {
        String accessToken = extractBearerToken(authorizationHeader);
        JWTClaimsSet claims = jwtHelper.validateTokenAndGetClaims(accessToken);

        String jti = claims.getJWTID();
        Instant expiresAt = claims.getExpirationTime().toInstant();
        accessTokenBlacklistService.blacklistToken(jti, Duration.between(Instant.now(), expiresAt));

        String refreshTokenHash = hashToken(request.getRefreshToken());
        refreshTokenRepo.findByTokenHash(refreshTokenHash)
                .ifPresent(refreshToken -> {
                    if (refreshToken.getRevokedAt() == null) {
                        refreshToken.setRevokedAt(LocalDateTime.now());
                        refreshTokenRepo.save(refreshToken);
                    }
                });
    }

    private void setRefreshToken(String refreshToken, User user) {
        String hashedToken = hashToken(refreshToken);
        RefreshToken refreshTokenEntity = buildRefreshToken(hashedToken, user);

        refreshTokenRepo.save(refreshTokenEntity);
    }

    private RefreshToken buildRefreshToken(String tokenHash, User user) {
        return RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }
}
