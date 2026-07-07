package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Tìm refresh token theo token string
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Tìm tất cả token hợp lệ của 1 user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId AND rt.revoked = false AND rt.expiryDate > CURRENT_TIMESTAMP")
    List<RefreshToken> findAllValidTokensByUser(@Param("userId") Long userId);
    
    /**
     * Thu hồi tất cả token của 1 user
     */
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId AND rt.revoked = false")
    void revokeAllByUserId(@Param("userId") Long userId);
}