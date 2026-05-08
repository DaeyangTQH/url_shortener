package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends BaseRepository<User, Long> {
    Optional<User> getUserByEmail(String email);
}
