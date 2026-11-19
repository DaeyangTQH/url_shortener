package com.haichann.url_shortener.user.repository;

import com.haichann.url_shortener.common.base.BaseRepository;
import com.haichann.url_shortener.user.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends BaseRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
