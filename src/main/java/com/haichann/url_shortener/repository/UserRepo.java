package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends BaseRepository<User, Long> {
}
