package com.haichann.url_shortener.url.service.impl;

import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.url.dto.CreateUrlRequest;
import com.haichann.url_shortener.url.dto.UrlResponse;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.url.helper.ShortCodeGenerator;
import com.haichann.url_shortener.url.repository.UrlShortenRepo;
import com.haichann.url_shortener.url.service.UrlService;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.repository.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlServiceImpl implements UrlService {

    private static final int MAX_SHORT_CODE_ATTEMPTS = 10;
    private static final int DEFAULT_EXPIRATION_DAYS = 30;

    UrlShortenRepo urlShortenRepo;
    UserRepo userRepo;
    ShortCodeGenerator shortCodeGenerator;

    @NonFinal
    @Value("${app.base-url}")
    String baseUrl;

    @Transactional
    @Override
    public UrlResponse create(String email, CreateUrlRequest request) {
        User user = getUserByEmailOrThrow(email);
        String normalizedUrl = validateAndNormalizeUrl(request.getOriginalUrl());

        LocalDateTime expirationDate = request.getExpirationDate() != null
                ? request.getExpirationDate()
                : LocalDateTime.now().plusDays(DEFAULT_EXPIRATION_DAYS);

        UrlShorten urlShorten = UrlShorten.builder()
                .user(user)
                .originalUrl(normalizedUrl)
                .shortCode(generateUniqueShortCode())
                .expirationDate(expirationDate)
                .isActive(true)
                .passwordHash(null)
                .maxClicks(null)
                .clickCount(0)
                .build();

        return toResponse(urlShortenRepo.save(urlShorten));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UrlResponse> listMine(String email) {
        User user = getUserByEmailOrThrow(email);
        return urlShortenRepo.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public UrlResponse getById(String email, Long id) {
        User user = getUserByEmailOrThrow(email);
        UrlShorten urlShorten = getOwnedUrlOrThrow(id, user);
        return toResponse(urlShorten);
    }

    @Transactional
    @Override
    public void deactivate(String email, Long id) {
        User user = getUserByEmailOrThrow(email);
        UrlShorten urlShorten = getOwnedUrlOrThrow(id, user);

        urlShorten.setIsActive(false);
        urlShorten.setIsDeleted(true);
        urlShorten.setDeletedAt(LocalDateTime.now());
        urlShorten.setUpdatedAt(LocalDateTime.now());
        urlShortenRepo.save(urlShorten);
    }

    @Transactional
    @Override
    public String resolveRedirect(String shortCode) {
        UrlShorten urlShorten = urlShortenRepo.findByShortCodeAndIsDeletedFalse(shortCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_not_found"));

        if (!Boolean.TRUE.equals(urlShorten.getIsActive())) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_inactive");
        }

        if (urlShorten.getExpirationDate() != null
                && urlShorten.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_expired");
        }

        int currentClicks = urlShorten.getClickCount() == null ? 0 : urlShorten.getClickCount();
        urlShorten.setClickCount(currentClicks + 1);
        urlShorten.setLastAccessedAt(LocalDateTime.now());
        urlShorten.setUpdatedAt(LocalDateTime.now());
        urlShortenRepo.save(urlShorten);

        return urlShorten.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_SHORT_CODE_ATTEMPTS; attempt++) {
            String shortCode = shortCodeGenerator.generate();
            if (!urlShortenRepo.existsByShortCode(shortCode)) {
                return shortCode;
            }
        }
        throw new AppException(ErrorCode.INTERNAL_ERROR);
    }

    private String validateAndNormalizeUrl(String originalUrl) {
        try {
            URI uri = URI.create(originalUrl.trim());
            String scheme = uri.getScheme();
            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null
                    || uri.getHost().isBlank()) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "error.url_invalid");
            }
            return uri.toString();
        } catch (IllegalArgumentException exception) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "error.url_invalid");
        }
    }

    private UrlShorten getOwnedUrlOrThrow(Long id, User user) {
        UrlShorten urlShorten = urlShortenRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_not_found"));
        assertOwner(urlShorten, user);
        return urlShorten;
    }

    private void assertOwner(UrlShorten urlShorten, User user) {
        if (urlShorten.getUser() == null || !urlShorten.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepo.getUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.user_not_found"));
    }

    private UrlResponse toResponse(UrlShorten urlShorten) {
        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;

        return UrlResponse.builder()
                .id(urlShorten.getId())
                .shortCode(urlShorten.getShortCode())
                .shortUrl(normalizedBaseUrl + "/r/" + urlShorten.getShortCode())
                .originalUrl(urlShorten.getOriginalUrl())
                .expirationDate(urlShorten.getExpirationDate())
                .isActive(urlShorten.getIsActive())
                .clickCount(urlShorten.getClickCount())
                .createdAt(urlShorten.getCreatedAt())
                .build();
    }
}
