package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.UrlShorten;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenRepo extends BaseRepository<UrlShorten, Long> {
}
