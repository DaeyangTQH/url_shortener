package com.haichann.url_shortener.user.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends BaseRepository<User, Long> {
    Optional<User> getUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
