package com.haichann.url_shortener.url.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.url.entity.UrlShorten;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlShortenRepo extends BaseRepository<UrlShorten, Long> {

    boolean existsByShortCode(String shortCode);

    Optional<UrlShorten> findByShortCodeAndIsDeletedFalse(String shortCode);

    Optional<UrlShorten> findByIdAndIsDeletedFalse(Long id);

    List<UrlShorten> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
}
