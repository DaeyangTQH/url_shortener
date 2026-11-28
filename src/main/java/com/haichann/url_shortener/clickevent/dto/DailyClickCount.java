package com.haichann.url_shortener.clickevent.dto;

import java.time.LocalDate;

public record DailyClickCount(LocalDate date, long clicks) {
}
