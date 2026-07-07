package com.example.demo.service.impl;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.interf.RefreshTokenService;
import com.example.demo.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements com.example.demo.service.interf.AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        // validate input
        if (request.getUsername() == null || request.getPassword() == null) {
            log.warn("Username or password is null");
            return null;
        }

        //check if user exists by username
        User user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new RuntimeException("Invalid username or password");
                });

        //check if password matches
        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        //check if user is active
        if(!user.getIsActive()) {
            log.warn("User is not active: {}", request.getUsername());
            throw new RuntimeException("User is not active");
        }


        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getId(), user.getRole().getNameRole().toString());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        //build response
        UserResponse userResponse = userMapper.toResponse(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        
        log.info("Refreshing access token using refresh token: {}", refreshToken);
        
        // verify refresh token
        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);

        // get user from refresh token
        User user = verifiedToken.getUser();

        // generate new access token
        String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getId(), user.getRole().getNameRole().toString());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        // build response
        UserResponse userResponse = userMapper.toResponse(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        // 1. Extract username from token
        String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        
        // 2. Find user
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 3. Revoke all refresh tokens
        refreshTokenService.revokeAllUserTokens(user.getId());
        
        log.info("User {} logged out successfully", username);
    }

}
