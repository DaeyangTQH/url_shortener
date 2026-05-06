package com.haichann.url_shortener.service;

import com.haichann.url_shortener.model.entity.User;

public interface UserService {
    User getMyProfile(String email);
    User updateMyInfo(String email, User user);
    User changePassword(String email, String newPasswordHash);
    User deactivateMyAccount(String email);
}
