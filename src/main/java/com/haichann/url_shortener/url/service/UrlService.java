package com.haichann.url_shortener.url.service;

import com.haichann.url_shortener.url.dto.CreateUrlRequest;
import com.haichann.url_shortener.url.dto.UrlResponse;

import java.util.List;

public interface UrlService {

    UrlResponse create(String email, CreateUrlRequest request);

    List<UrlResponse> listMine(String email);

    UrlResponse getById(String email, Long id);

    void deactivate(String email, Long id);

    String resolveRedirect(String shortCode);
}
