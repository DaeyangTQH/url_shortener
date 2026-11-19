package com.haichann.url_shortener.auth.oauth;

import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.auth.security.JwtHelper;
import com.haichann.url_shortener.auth.oauth.OAuthUserProfile;
import com.haichann.url_shortener.auth.dto.AuthenticationResponse;
import com.haichann.url_shortener.auth.entity.RefreshToken;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.entity.UserIdentity;
import com.haichann.url_shortener.user.enums.AuthProvider;
import com.haichann.url_shortener.user.enums.UserStatus;
import com.haichann.url_shortener.auth.repository.RefreshTokenRepo;
import com.haichann.url_shortener.user.repository.RoleRepo;
import com.haichann.url_shortener.user.repository.UserIdentityRepo;
import com.haichann.url_shortener.user.repository.UserRepo;
import com.haichann.url_shortener.auth.oauth.OAuth2Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoogleOAuth2ServiceImpl implements OAuth2Service {

    UserIdentityRepo userIdentityRepo;
    UserRepo userRepo;
    RoleRepo roleRepo;
    RefreshTokenRepo refreshTokenRepo;
    JwtHelper jwtHelper;

    @NonFinal
    @Value("${jwt.refresh-expiration-days}")
    long refreshExpirationDays;

    @Override
    public AuthProvider getAuthProvider() {
        return AuthProvider.GOOGLE;
    }

    @Transactional
    @Override
    public AuthenticationResponse processLogin(OAuthUserProfile profile) {
        validateProfile(profile);

        Optional<UserIdentity> existingIdentity =
                userIdentityRepo.findByProviderAndProviderUserId(
                        profile.getProvider(), profile.getProviderUserId());

        User user;
        if (existingIdentity.isPresent()) { // User has previously logged in with this Google account
            user = existingIdentity.get().getUser();
        } else { // New Google account, check if a local user with the same email exists
            Optional<User> existingUser = userRepo.getUserByEmail(profile.getEmail());
            if (existingUser.isPresent()) { // Local user exists, link the Google account to this user
                user = existingUser.get();
                ensureNoConflictingGoogleIdentity(user, profile);
                createUserIdentity(user, profile, false);
            } else { // No local user exists, create a new user and link the Google account
                user = createOAuthUser(profile);
                createUserIdentity(user, profile, true);
            }
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }

        user.setLastLoginAt(LocalDateTime.now());
        if (profile.isEmailVerified()) {
            user.setEmailVerified(true);
        }
        if (StringUtils.hasText(profile.getAvatarUrl())) {
            user.setAvatarImageUrl(profile.getAvatarUrl());
        }
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

    // Validate that the OAuth profile contains all required information
    private void validateProfile(OAuthUserProfile profile) {
        if (!StringUtils.hasText(profile.getProviderUserId())
                || !StringUtils.hasText(profile.getEmail())
                || !profile.isEmailVerified()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.oauth_profile_invalid");
        }
    }

    // Ensure that the user does not already have a different Google identity linked
    private void ensureNoConflictingGoogleIdentity(User user, OAuthUserProfile profile) {
        userIdentityRepo.findByUserIdAndProvider(user.getId(), AuthProvider.GOOGLE)
                .ifPresent(identity -> {
                    if (!identity.getProviderUserId().equals(profile.getProviderUserId())) {
                        throw new AppException(ErrorCode.CONFLICT, "error.google_account_mismatch");
                    }
                });
    }

    private User createOAuthUser(OAuthUserProfile profile) {
        var userRole = roleRepo.findByRoleName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR, "error.default_user_role_missing"));

        User user = User.builder()
                .username(generateUsername(profile.getEmail()))
                .email(profile.getEmail())
                .passwordHash(null)
                .role(userRole)
                .status(UserStatus.ACTIVE)
                .emailVerified(profile.isEmailVerified())
                .avatarImageUrl(profile.getAvatarUrl() != null ? profile.getAvatarUrl() : "")
                .build();

        return userRepo.save(user);
    }

    private void createUserIdentity(User user, OAuthUserProfile profile, boolean isPrimary) {
        UserIdentity identity = UserIdentity.builder()
                .user(user)
                .provider(profile.getProvider())
                .providerUserId(profile.getProviderUserId())
                .linkedAt(LocalDateTime.now())
                .isPrimary(isPrimary)
                .build();
        userIdentityRepo.save(identity);
    }

    private String generateUsername(String email) {
        String localPart = email.substring(0, email.indexOf('@'));
        String base = localPart.replaceAll("[^a-zA-Z0-9._-]", "");
        if (!StringUtils.hasText(base)) {
            base = "user";
        }

        String candidate = base;
        int suffix = 1;
        while (userRepo.existsByUsername(candidate)) {
            candidate = base + "_" + suffix;
            suffix++;
        }
        return candidate;
    }

    private void setRefreshToken(String refreshToken, User user) {
        refreshTokenRepo.save(buildRefreshToken(hashToken(refreshToken), user));
    }

    private RefreshToken buildRefreshToken(String tokenHash, User user) {
        return RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();
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