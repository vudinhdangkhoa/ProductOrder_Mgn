package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.TokenSSOInfoResponse;
import com.example.demo.dto.response.TokenSSOResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.interf.SsoService;
import com.example.demo.service.interf.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/api/sso")
@RequiredArgsConstructor

@Slf4j
@Tag(name = "SSO", description = "Single Sign-On API")
public class SSOController {

    private final SsoService ssoService;
    private final UserService userService;

    // 1. Lấy URL đăng nhập (FE gọi để lấy redirect URL)
    @GetMapping("/login-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getLoginUrl(@RequestParam(required = false) String state) {
        String loginUrl = ssoService.getAuthorizationUrl(state != null ? state : "default_state");
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Login URL obtained successfully")
                .data(Map.of("loginUrl", loginUrl))
                .build());
    }

    // 2. Callback từ SSO - Đổi code lấy token 
    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<TokenSSOResponse>> callback(@RequestParam String code, 
                                                         @RequestParam(required = false) String state) {
        log.info("Received callback with code: {} and state: {}", code, state);
        
        // Đổi code lấy token
        TokenSSOResponse tokenResponse = ssoService.exchangeCodeForToken(code);
        
        if (tokenResponse != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenResponse.getAccessToken());
            response.put("refreshToken", tokenResponse.getRefreshToken());
            response.put("expiresIn", tokenResponse.getExpiresIn());
            response.put("userInfo",tokenResponse.getUserInfo());
            response.put("refreshTokenLocal",tokenResponse.getRefreshTokenLocal());

            //check user lock
            UserResponse checkUserLock = userService.getUserByUsernameAndIsDeletedFalse(tokenResponse.getUserInfo().getUserName());
           
            return ResponseEntity.ok(ApiResponse.<TokenSSOResponse>builder()
                    .success(true)
                    .message("Token obtained successfully")
                    .data(tokenResponse)
                    .build());
        }
        
        return ResponseEntity.status(500).body(ApiResponse.<TokenSSOResponse>builder()
                .success(false)
                .message("Failed to exchange code for token")
                .build());
    }
    
    // 3. Refresh token 
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenSSOResponse>> refreshToken(@RequestParam String refreshToken) {
        TokenSSOResponse tokenResponse = ssoService.refreshToken(refreshToken);
        
        if (tokenResponse != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenResponse.getAccessToken());
            response.put("refreshToken", tokenResponse.getRefreshToken());
            response.put("expiresIn", tokenResponse.getExpiresIn());
            return ResponseEntity.ok(ApiResponse.<TokenSSOResponse>builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(tokenResponse)
                    .build());
        }

        return ResponseEntity.status(500).body(ApiResponse.<TokenSSOResponse>builder()
                .success(false)
                .message("Failed to refresh token")
                .build());
    }

    // 4. Logout 
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        ssoService.logout(token);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Logged out successfully")
                .build());
    }

    // 5.  Verify token (Introspect)
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<TokenSSOInfoResponse>> verifyToken(@RequestParam String token) {

        TokenSSOInfoResponse tokenInfo = ssoService.verifyToken(token);

        return ResponseEntity.ok(ApiResponse.<TokenSSOInfoResponse>builder()
                .success(true)
                .message("Token verified successfully")
                .data(tokenInfo)
                .build());
    }
}
