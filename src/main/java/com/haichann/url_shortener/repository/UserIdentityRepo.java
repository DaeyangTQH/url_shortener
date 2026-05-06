package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.UserIdentity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserIdentityRepo extends BaseRepository<UserIdentity, Long> {
}
