package com.haichann.url_shortener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.UserIdentity;
import com.haichann.url_shortener.model.enums.AuthProvider;

@Repository
public interface UserIdentityRepo extends BaseRepository<UserIdentity, Long> {

    Optional<UserIdentity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    Optional<UserIdentity> findByUserIdAndProvider(Long userId, AuthProvider provider);

    List<UserIdentity> findByUserId(Long userId);
}
