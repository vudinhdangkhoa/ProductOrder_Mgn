package com.example.demo.service.impl;


import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.interf.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

   @Value("${app.jwt.refresh-expiration-ms:604800000}") // Mặc định 7 ngày
    private long refreshTokenDurationMs;

    // tạo refresh token mới cho user
    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {

        // Kiểm tra xem user có tồn tại không
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        //xóa tất cả refresh token cũ của user trước khi tạo mới
        revokeAllUserTokens(userId);

        //tạo refresh token mới
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Created new refresh token for userId {}: {}", userId, user.getUsername());
        
        

        return savedToken;
    }

    //Xác thức refresh token
    @Override
    public RefreshToken verifyRefreshToken(String token) {

        // tìm refresh token theo token string
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Refresh token not found: " + token));

        // kiểm tra xem token đã bị thu hồi chưa
        if (refreshToken.getRevoked()) {
            log.warn("Refresh token has been revoked: {}", token);
            throw new RuntimeException("Refresh token has been revoked");
        }

        //kiểm tra xem token đã hết hạn chưa
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token has expired: {}", token);
            throw new RuntimeException("Refresh token has expired");
        }

        return refreshToken;

    }

    // Thu hồi tất cả token của user
    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
       
        var validTokens = refreshTokenRepository.findAllValidTokensByUser(userId);
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
            log.info("Revoked all refresh tokens for userId {}", userId);
        } else {
            log.info("No valid refresh tokens found for userId {}", userId);
        }

    }

    //xóa refresh token khi hết hạn
    @Override
    @Transactional
    public void deleteExpiredTokens() {
        var expiredTokens = refreshTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(LocalDateTime.now()))
                .toList();

        if (!expiredTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(expiredTokens);
            log.info("Deleted {} expired refresh tokens", expiredTokens.size());
        } else {
            log.info("No expired refresh tokens to delete");
        }
    }

}
