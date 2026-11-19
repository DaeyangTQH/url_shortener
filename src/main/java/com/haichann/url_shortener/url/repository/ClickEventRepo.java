package com.haichann.url_shortener.url.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.url.entity.ClickEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickEventRepo extends BaseRepository<ClickEvent, Long> {
}
