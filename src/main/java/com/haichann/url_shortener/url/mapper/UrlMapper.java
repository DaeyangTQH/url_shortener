package com.haichann.url_shortener.url.mapper;

import com.haichann.url_shortener.url.dto.UrlResponse;
import com.haichann.url_shortener.url.entity.UrlShorten;
import com.haichann.url_shortener.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UrlMapper {
    @Mapping(target = "shortUrl", expression = "java(shortUrlPrefix + url.getShortCode())")
    UrlResponse toResponse(UrlShorten url, @Context String shortUrlPrefix);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "originalUrl", source = "originalUrl")
    @Mapping(target = "shortCode", source = "shortCode")
    @Mapping(target = "expirationDate", source = "expirationDate")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "clickCount", constant = "0")
    UrlShorten toEntity(User user, String originalUrl, String shortCode, LocalDateTime expirationDate);
}
