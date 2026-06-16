package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends BaseRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
