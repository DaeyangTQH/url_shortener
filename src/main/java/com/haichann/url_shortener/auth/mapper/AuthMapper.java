package com.haichann.url_shortener.auth.mapper;

import com.haichann.url_shortener.auth.dto.AuthenticationResponse;
import com.haichann.url_shortener.auth.dto.LocalRegisterRequest;
import com.haichann.url_shortener.auth.entity.RefreshToken;
import com.haichann.url_shortener.auth.oauth.OAuthUserProfile;
import com.haichann.url_shortener.user.entity.Role;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.entity.UserIdentity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AuthMapper {
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "authenticated", constant = "true")
    AuthenticationResponse toAuthenticationResponse(String accessToken, String refreshToken);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "username", source = "request.username")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "avatarImageUrl", expression = "java(\"\")")
    User toLocalUser(LocalRegisterRequest request, Role role, String passwordHash);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "profile.email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "emailVerified", source = "profile.emailVerified")
    @Mapping(target = "avatarImageUrl", source = "profile.avatarUrl", defaultExpression = "java(\"\")")
    User toOAuthUser(OAuthUserProfile profile, Role role, String username);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "provider", source = "profile.provider")
    @Mapping(target = "providerUserId", source = "profile.providerUserId")
    @Mapping(target = "linkedAt", source = "linkedAt")
    @Mapping(target = "isPrimary", source = "primary")
    UserIdentity toUserIdentity(User user, OAuthUserProfile profile, LocalDateTime linkedAt, boolean primary);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "tokenHash", source = "tokenHash")
    @Mapping(target = "expiresAt", source = "expiresAt")
    RefreshToken toRefreshToken(String tokenHash, User user, LocalDateTime expiresAt);

    @BeanMapping(builder = @Builder(disableBuilder = false))
    @Mapping(target = "provider", constant = "GOOGLE")
    @Mapping(target = "emailVerified", constant = "true")
    OAuthUserProfile toGoogleProfile(String providerUserId, String email, String displayName, String avatarUrl);
}
