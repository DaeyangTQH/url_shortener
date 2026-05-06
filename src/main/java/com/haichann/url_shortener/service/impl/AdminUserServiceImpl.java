package com.haichann.url_shortener.service.impl;

import com.haichann.url_shortener.model.entity.Role;
import com.haichann.url_shortener.model.entity.User;
import com.haichann.url_shortener.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public User findUserById(Long id) {
        return null;
    }

    @Override
    public User updateRole(Long id, Role role) {
        return null;
    }

    @Override
    public User banUser(Long id) {
        return null;
    }

    @Override
    public User unbanUser(Long id) {
        return null;
    }

    @Override
    public User restoreUser(Long id) {
        return null;
    }

    @Override
    public User deletePermanentUser(Long id) {
        return null;
    }
}
