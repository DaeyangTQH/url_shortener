package com.haichann.url_shortener.user.service;

import com.haichann.url_shortener.user.dto.ChangePasswordRequest;
import com.haichann.url_shortener.user.dto.UpdateMyInfoRequest;
import com.haichann.url_shortener.user.dto.ProfileDTO;

public interface UserService {
    ProfileDTO getMyProfile(String email);
    ProfileDTO updateMyInfo(String email, UpdateMyInfoRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void deactivateMyAccount(String email);
}
