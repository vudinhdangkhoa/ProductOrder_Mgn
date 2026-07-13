package com.example.demo.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.dto.response.UserInfoSSOResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class DecodeToken {

    public static Map<String, Object> decodeToken(String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token");
            }

            // Decode payload với UTF-8
            String payload = parts[1];

            // Sử dụng Base64.getUrlDecoder() và chuyển thành String với UTF-8
            byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(decodedPayload, Map.class);

            log.info("Decoded token claims: {}", claims);
            return claims;

        } catch (Exception e) {
            log.error("Error decoding token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Decode và lấy thông tin user profile
     */
    public static UserInfoSSOResponse getUserInfoFromToken(String accessToken) {
        Map<String, Object> claims = decodeToken(accessToken);
        if (claims == null) {
            return null;
        }

        UserInfoSSOResponse userInfo = UserInfoSSOResponse.builder()
                .sub((String) claims.get("sub"))
                .build();

        // Lấy email
        if (claims.containsKey("email")) {
            Map<String, Object> emailObj = (Map<String, Object>) claims.get("email");
            userInfo.setEmail((String) emailObj.get("email"));
        }

        // Lấy profile
        if (claims.containsKey("profile")) {
            Map<String, Object> profile = (Map<String, Object>) claims.get("profile");
            userInfo.setFullName((String) profile.get("full_name"));
            userInfo.setUserName((String) profile.get("user_name"));
            userInfo.setDepartment((String) profile.get("department"));
            userInfo.setPosition((String) profile.get("position"));
            userInfo.setAvatar((String) profile.get("avatar"));
            userInfo.setTelegram((String) profile.get("telegram"));
        }

        return userInfo;
    }

}
