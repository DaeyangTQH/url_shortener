package com.haichann.url_shortener.repository;

import com.haichann.url_shortener.base.BaseRepository;
import com.haichann.url_shortener.model.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends BaseRepository<Role, Long> {
}
