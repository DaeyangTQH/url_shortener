package com.haichann.url_shortener.service;

import com.haichann.url_shortener.model.dto.request.ChangePasswordRequest;
import com.haichann.url_shortener.model.dto.request.UpdateMyInfoRequest;
import com.haichann.url_shortener.model.dto.response.ProfileDTO;

public interface UserService {
    ProfileDTO getMyProfile(String email);
    ProfileDTO updateMyInfo(String email, UpdateMyInfoRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void deactivateMyAccount(String email);
}
