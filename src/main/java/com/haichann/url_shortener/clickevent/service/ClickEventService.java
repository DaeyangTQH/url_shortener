package com.haichann.url_shortener.clickevent.service;

import com.haichann.url_shortener.clickevent.dto.DailyClickCount;
import com.haichann.url_shortener.url.entity.UrlShorten;

import java.time.LocalDateTime;
import java.util.List;

public interface ClickEventService {
    void record(UrlShorten url, LocalDateTime clickedAt);

    List<DailyClickCount> getDailyStats(String email, Long urlId, int days);
}
