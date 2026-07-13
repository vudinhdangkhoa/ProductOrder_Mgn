package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TokenSSOResponse {
    private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private String scope;
        private UserInfoSSOResponse userInfo;
        private String refreshTokenLocal;
        private String accessTokenLocal;
}


