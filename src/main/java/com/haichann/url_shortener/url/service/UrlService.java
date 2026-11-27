package com.haichann.url_shortener.url.service;

import com.haichann.url_shortener.common.base.PageResponse;
import com.haichann.url_shortener.url.dto.CreateUrlRequest;
import com.haichann.url_shortener.url.dto.UrlResponse;
import org.springframework.data.domain.Pageable;

public interface UrlService {

    UrlResponse create(String email, CreateUrlRequest request);

    PageResponse<UrlResponse> listMine(String email, Pageable pageable);

    UrlResponse getById(String email, Long id);

    void deactivate(String email, Long id);

    String resolveRedirect(String shortCode);
}
