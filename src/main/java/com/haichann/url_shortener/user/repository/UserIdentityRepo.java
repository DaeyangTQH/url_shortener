package com.haichann.url_shortener.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.user.entity.UserIdentity;
import com.haichann.url_shortener.user.enums.AuthProvider;

@Repository
public interface UserIdentityRepo extends BaseRepository<UserIdentity, Long> {

    Optional<UserIdentity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    Optional<UserIdentity> findByUserIdAndProvider(Long userId, AuthProvider provider);

    List<UserIdentity> findByUserId(Long userId);
}
