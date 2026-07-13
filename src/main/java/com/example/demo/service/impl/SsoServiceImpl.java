package com.example.demo.service.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.response.TokenSSOInfoResponse;
import com.example.demo.dto.response.TokenSSOResponse;
import com.example.demo.dto.response.UserInfoSSOResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.interf.RefreshTokenService;
import com.example.demo.service.interf.SsoService;
import com.example.demo.util.DecodeToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

    
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SsoServiceImpl implements SsoService {

    @Value("${app.sso.server-url}")
    private String ssoServerUrl;

    @Value("${app.sso.client-id}")
    private String clientId;

    @Value("${app.sso.client-secret}")
    private String clientSecret;

    @Value("${app.sso.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate ;
    private final ObjectMapper objectMapper ;
    private final UserRepository UserRepository;
    private final UserMapper Usermapper;
    private final RoleRepository RoleRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String getAuthorizationUrl(String state) {

        return ssoServerUrl + "/oauth2/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=openid%20profile%20email" +
                "&state=" + state;

    }

    @Override
    @Transactional
    // 2. Đổi code lấy token
    public TokenSSOResponse exchangeCodeForToken(String code) {
        try {
            String url = ssoServerUrl + "/oauth2/token";
            
            // Tạo Basic Auth header
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            // Body request
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
//            body.add("client_id",clientId);
//            body.add("client_secret",clientSecret);
            body.add("code", code);
            body.add("redirect_uri", redirectUri);
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedCredentials);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode json = objectMapper.readTree(response.getBody());
                UserInfoSSOResponse userInfo = DecodeToken.getUserInfoFromToken(json.get("access_token").asText());
                
                // Kiểm tra xem user có tồn tại trong DB không, nếu chưa có thì tạo mới
                User existingUser = UserRepository.findByUsernameAndIsDeletedFalse(userInfo.getUserName()).orElse(null);
                if (existingUser == null) {
                     existingUser = Usermapper.toEntityFromSSO(userInfo);
                    Role defaultRole = RoleRepository.findByNameRole(UserRole.DEFAULT).orElseThrow(() -> new RuntimeException("Default role not found"));
                    existingUser.setRole(defaultRole);
                    UserRepository.save(existingUser);
                }

                //tạo response token
               
                RefreshToken refreshTokenLocal = refreshTokenService.createRefreshToken(existingUser.getId());
                refreshTokenLocal.setTokenSSO(json.get("refresh_token").asText());
                refreshTokenRepository.save(refreshTokenLocal);
                String accessTokenLocal = jwtTokenProvider.generateToken(existingUser.getUsername(), existingUser.getId(), existingUser.getRole().getNameRole().toString());


                TokenSSOResponse tokenResponse = TokenSSOResponse.builder()
                        .accessToken(json.get("access_token").asText())
                        .refreshToken(json.get("refresh_token").asText())
                        .tokenType(json.get("token_type").asText())
                        .expiresIn(json.get("expires_in").asLong())
                        .userInfo(userInfo)
                        .refreshTokenLocal(refreshTokenLocal.getToken())
                        .accessTokenLocal(accessTokenLocal)
                        .scope(json.get("scope").asText())
                        .build();
                
                log.info("Token exchanged successfully for client: {}", clientId);
                return tokenResponse;
            }
        } catch (Exception e) {
            log.error("Error exchanging code for token: {}", e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    // 3. Verify token (Introspect)
    public TokenSSOInfoResponse verifyToken(String token) {
        try {
            String url = ssoServerUrl + "/oauth2/introspect";
            
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("token", token);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedCredentials);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode json = objectMapper.readTree(response.getBody());
                boolean active = json.get("active").asBoolean();
                
                if (active) {
                    // Lấy thông tin user từ token
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("sub", json.get("sub").asText());
                    
                    // Email
                    if (json.has("email") && json.get("email").has("email")) {
                        userInfo.put("email", json.get("email").get("email").asText());
                    }
                    
                    // Profile
                    if (json.has("profile")) {
                        JsonNode profile = json.get("profile");
                        userInfo.put("fullName", profile.has("full_name") ? profile.get("full_name").asText() : null);
                        userInfo.put("userName", profile.has("user_name") ? profile.get("user_name").asText() : null);
                        userInfo.put("department", profile.has("department") ? profile.get("department").asText() : null);
                        userInfo.put("position", profile.has("position") ? profile.get("position").asText() : null);
                        userInfo.put("avatar", profile.has("avatar") ? profile.get("avatar").asText() : null);
                    }
                    
                    log.debug("Token verified for user: {}", userInfo.get("fullName"));
                    
                    return TokenSSOInfoResponse.builder()
                            .active(true)
                            .userInfo(userInfo)
                            .build();
                } else {
                    log.warn("Token is not active");
                }
            }
        } catch (Exception e) {
            log.error("Error verifying token: {}", e.getMessage(), e);
        }
        return TokenSSOInfoResponse.builder().active(false).userInfo(null).build();
    }

    @Override
    // 4. Refresh token
    public TokenSSOResponse refreshToken(String refreshToken) {
        try {
            String url = ssoServerUrl + "/oauth2/token";
            
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("refresh_token", refreshToken);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedCredentials);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode json = objectMapper.readTree(response.getBody());
                
                TokenSSOResponse tokenResponse = TokenSSOResponse.builder()
                        .accessToken(json.get("access_token").asText())
                        .refreshToken(json.get("refresh_token").asText())
                        .tokenType(json.get("token_type").asText())
                        .expiresIn(json.get("expires_in").asLong())
                        .scope(json.get("scope").asText())
                        .build();
                
                log.info("Token refreshed successfully");
                return tokenResponse;
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    // 5. Logout
    public void logout(String token) {
        try {
            String url = "https://10.1.3.197:3004/sso/logout";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.postForEntity(url, request, String.class);
            
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Error logging out: {}", e.getMessage(), e);
        }
    }
}
