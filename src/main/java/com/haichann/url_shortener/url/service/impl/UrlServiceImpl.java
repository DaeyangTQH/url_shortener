package com.haichann.url_shortener.url.service.impl;

import com.haichann.url_shortener.clickevent.service.ClickEventService;
import com.haichann.url_shortener.common.base.PageResponse;
import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.config.AppProperties;
import com.haichann.url_shortener.url.dto.CreateUrlRequest;
import com.haichann.url_shortener.url.dto.UrlResponse;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.url.helper.ShortCodeGenerator;
import com.haichann.url_shortener.url.mapper.UrlMapper;
import com.haichann.url_shortener.url.repository.UrlShortenRepo;
import com.haichann.url_shortener.url.service.UrlService;
import com.haichann.url_shortener.user.entity.User;
import com.haichann.url_shortener.user.repository.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlServiceImpl implements UrlService {

    private static final int MAX_SHORT_CODE_ATTEMPTS = 10;
    private static final int DEFAULT_EXPIRATION_DAYS = 30;

    UrlShortenRepo urlShortenRepo;
    UserRepo userRepo;
    ShortCodeGenerator shortCodeGenerator;
    ClickEventService clickEventService;
    UrlMapper urlMapper;

    AppProperties appProperties;

    @Transactional
    @Override
    public UrlResponse create(String email, CreateUrlRequest request) {
        User user = getUserByEmailOrThrow(email);
        String normalizedUrl = validateAndNormalizeUrl(request.getOriginalUrl());

        LocalDateTime expirationDate = request.getExpirationDate() != null
                ? request.getExpirationDate()
                : LocalDateTime.now().plusDays(DEFAULT_EXPIRATION_DAYS);

        UrlShorten urlShorten = urlMapper.toEntity(user, normalizedUrl, generateUniqueShortCode(), expirationDate);

        return urlMapper.toResponse(urlShortenRepo.save(urlShorten), appProperties.getShortUrlPrefix());
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<UrlResponse> listMine(String email, Pageable pageable) {
        User user = getUserByEmailOrThrow(email);
        Page<UrlResponse> page = urlShortenRepo
                .findByUserIdAndIsDeletedFalse(user.getId(), pageable)
                .map(url -> urlMapper.toResponse(url, appProperties.getShortUrlPrefix()));
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    @Override
    public UrlResponse getById(String email, Long id) {
        User user = getUserByEmailOrThrow(email);
        UrlShorten urlShorten = getOwnedUrlOrThrow(id, user);
        return urlMapper.toResponse(urlShorten, appProperties.getShortUrlPrefix());
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

        LocalDateTime now = LocalDateTime.now();
        if (urlShorten.getExpirationDate() != null
                && urlShorten.getExpirationDate().isBefore(now)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_expired");
        }

        if (urlShortenRepo.incrementClicksIfActive(urlShorten.getId(), now) == 0) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_not_found");
        }
        clickEventService.record(urlShorten, now);

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
        return urlShortenRepo.findByIdAndUserIdAndIsDeletedFalse(id, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_not_found"));
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepo.getUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.user_not_found"));
    }
}
