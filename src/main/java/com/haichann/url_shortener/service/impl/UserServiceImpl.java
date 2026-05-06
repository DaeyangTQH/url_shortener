package com.haichann.url_shortener.service.impl;

import com.haichann.url_shortener.model.entity.User;
import com.haichann.url_shortener.repository.UserRepo;
import com.haichann.url_shortener.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepo userRepo;

    @Override
    public User getMyProfile(String email) {
        return null;
    }

    @Override
    public User updateMyInfo(String email, User user) {
        return null;
    }

    @Override
    public User changePassword(String email, String newPasswordHash) {
        return null;
    }

    @Override
    public User deactivateMyAccount(String email) {
        return null;
    }
}
