package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.ClickEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickEventRepo extends BaseRepository<ClickEvent, Long> {
}
