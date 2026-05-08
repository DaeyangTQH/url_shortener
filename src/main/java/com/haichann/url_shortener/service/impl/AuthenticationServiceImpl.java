package com.haichann.url_shortener.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl {

    @Value("${jwt.secret-key}")
    private String SIGNER_KEY;
}
