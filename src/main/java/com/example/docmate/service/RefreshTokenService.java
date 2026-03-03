package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;

public interface RefreshTokenService {
    void createRefreshToken(String userEmail,String refreshToken);
    GlobalResponse rotateAccessToken(String refreshToken);
}
