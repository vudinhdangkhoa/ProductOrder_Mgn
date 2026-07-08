package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.service.interf.AuthService;
import com.example.demo.util.SecurityUtils;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API xác thực người dùng")
public class AuthController {

    private final AuthService authService;
    private final SecurityUtils securityUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> postMethodName(@Valid @RequestBody LoginRequest request) {

        log.info("REST request to login user: {}", request.getUsername());

        LoginResponse response = authService.login(request);

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .data(response)
                .message("Login successful")
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletRequest request) {

        log.info("REST request to logout user");

        String accessToken = securityUtils.extractToken(request);

        authService.logout(accessToken);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data("Logout successful")
                .message("Logout successful")
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        LoginResponse loginResponse
                = authService.refreshToken(request.getRefreshToken());

        ApiResponse<LoginResponse> apiResponse
                = ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(loginResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }
    

}
