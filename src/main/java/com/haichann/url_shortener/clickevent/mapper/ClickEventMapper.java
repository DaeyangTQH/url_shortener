package com.haichann.url_shortener.clickevent.mapper;

import com.haichann.url_shortener.clickevent.entity.ClickEvent;
import com.haichann.url_shortener.url.entity.UrlShorten;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ClickEventMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "urlShorten", source = "url")
    @Mapping(target = "clickedAt", source = "clickedAt")
    ClickEvent toEntity(UrlShorten url, LocalDateTime clickedAt);
}
