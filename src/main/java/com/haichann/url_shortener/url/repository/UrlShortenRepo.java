package com.haichann.url_shortener.url.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.url.entity.UrlShorten;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlShortenRepo extends BaseRepository<UrlShorten, Long> {

    boolean existsByShortCode(String shortCode);

    Optional<UrlShorten> findByShortCodeAndIsDeletedFalse(String shortCode);

    @Modifying
    @Query(value = """
            UPDATE url_shorten
            SET click_count = COALESCE(click_count, 0) + 1,
                last_accessed_at = :now,
                updated_at = :now
            WHERE id = :id
              AND is_active = true
              AND is_deleted = false
              AND (expiration_date IS NULL OR expiration_date >= :now)
            """, nativeQuery = true)
    int incrementClicksIfActive(@Param("id") Long id, @Param("now") LocalDateTime now);

    Optional<UrlShorten> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    boolean existsByIdAndUserEmailAndIsDeletedFalse(Long id, String email);

    Page<UrlShorten> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
