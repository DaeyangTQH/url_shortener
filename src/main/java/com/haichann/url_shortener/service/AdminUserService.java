package com.haichann.url_shortener.service;

import com.haichann.url_shortener.model.entity.Role;
import com.haichann.url_shortener.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    Page<User> findAllUsers(Pageable pageable);
    User findUserByEmail(String email);
    User findUserById(Long id);
    User updateRole(Long id, Role role);
    User banUser(Long id);
    User unbanUser(Long id);
    User restoreUser(Long id);
    User deletePermanentUser(Long id);
}