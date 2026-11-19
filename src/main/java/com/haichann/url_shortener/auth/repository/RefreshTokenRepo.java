package com.haichann.url_shortener.auth.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.auth.entity.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends BaseRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
