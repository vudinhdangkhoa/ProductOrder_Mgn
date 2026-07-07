package com.example.demo.service.interf;

import com.example.demo.entity.RefreshToken;

public interface RefreshTokenService {
    
    /**
     * Tạo refresh token mới cho user
     */
    RefreshToken createRefreshToken(Long userId);
    
    /**
     * Xác thực refresh token
     */
    RefreshToken verifyRefreshToken(String token);
    
    /**
     * Thu hồi tất cả token của user
     */
    void revokeAllUserTokens(Long userId);
    
    /**
     * Xóa refresh token khi hết hạn
     */
    void deleteExpiredTokens();
}