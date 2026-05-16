package com.example.docmate.service.impl;

import com.example.docmate.entity.RefreshTokenEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.repository.RefreshTokenRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.RefreshTokenService;
import com.example.docmate.utils.JwtUtils;
import com.example.docmate.utils.MyConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days for refresh token
    private Long refreshTokenExpiration;


    public void createRefreshToken(String userEmail,String refreshToken) {
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException("User "+ MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        refreshTokenRepository.deleteByUserId(userEntity.getId());

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(userEntity);
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

    }


    public GlobalResponse rotateAccessToken(String refreshToken) {

        refreshToken = refreshToken
                .replace("\r", "")
                .replace("\n", "")
                .trim();

        if (!jwtUtils.extractTokenType(refreshToken).equals("REFRESH")) {
            throw new GlobalException("Invalid token type", HttpStatus.BAD_REQUEST);
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (jwtUtils.isTokenExpired(refreshTokenEntity.getToken())) {
            refreshTokenRepository.delete(refreshTokenEntity); // clean up
            throw new GlobalException("Refresh token expired. Please log in again.", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new GlobalException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtUtils.extractUsername(refreshToken);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        String newAccessToken = jwtUtils.generateAccessToken(userEntity.getEmail(), userEntity.getRole().getName(), userEntity.getId());

        return GlobalResponseBuilder.buildSuccessResponseWithData("Access token rotated successfully", newAccessToken);
    }

}
