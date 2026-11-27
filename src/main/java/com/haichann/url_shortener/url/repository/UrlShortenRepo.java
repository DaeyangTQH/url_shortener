package com.haichann.url_shortener.url.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.url.entity.UrlShorten;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenRepo extends BaseRepository<UrlShorten, Long> {

    boolean existsByShortCode(String shortCode);

    Optional<UrlShorten> findByShortCodeAndIsDeletedFalse(String shortCode);

    Optional<UrlShorten> findByIdAndIsDeletedFalse(Long id);

    Page<UrlShorten> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
