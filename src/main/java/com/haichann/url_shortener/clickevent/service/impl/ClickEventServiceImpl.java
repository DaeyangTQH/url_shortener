package com.haichann.url_shortener.clickevent.service.impl;

import com.haichann.url_shortener.clickevent.dto.DailyClickCount;
import com.haichann.url_shortener.clickevent.mapper.ClickEventMapper;
import com.haichann.url_shortener.clickevent.repository.ClickEventRepo;
import com.haichann.url_shortener.clickevent.service.ClickEventService;
import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.url.repository.UrlShortenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClickEventServiceImpl implements ClickEventService {
    private final ClickEventRepo clickEventRepo;
    private final UrlShortenRepo urlShortenRepo;
    private final ClickEventMapper clickEventMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void record(UrlShorten url, LocalDateTime clickedAt) {
        clickEventRepo.save(clickEventMapper.toEntity(url, clickedAt));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyClickCount> getDailyStats(String email, Long urlId, int days) {
        if (days < 1 || days > 30) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (!urlShortenRepo.existsByIdAndUserEmailAndIsDeletedFalse(urlId, email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "error.url_not_found");
        }

        LocalDate end = LocalDate.now().plusDays(1);
        LocalDate start = end.minusDays(days);
        List<DailyClickCount> counts = clickEventRepo.countByDay(urlId, start.atStartOfDay(), end.atStartOfDay());

        List<DailyClickCount> result = new ArrayList<>(days);
        int next = 0;
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            if (next < counts.size() && counts.get(next).date().equals(date)) {
                result.add(counts.get(next++));
            } else {
                result.add(new DailyClickCount(date, 0));
            }
        }
        return result;
    }
}
