package com.haichann.url_shortener.url.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.url.entity.UrlShorten;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenRepo extends BaseRepository<UrlShorten, Long> {
}
