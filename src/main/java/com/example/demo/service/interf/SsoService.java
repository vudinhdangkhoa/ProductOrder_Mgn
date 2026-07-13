package com.example.demo.service.interf;

import com.example.demo.dto.response.TokenSSOInfoResponse;
import com.example.demo.dto.response.TokenSSOResponse;

public interface SsoService {
     public String getAuthorizationUrl(String state);

     public TokenSSOResponse exchangeCodeForToken(String code);

    public TokenSSOInfoResponse verifyToken(String token);

      public TokenSSOResponse refreshToken(String refreshToken);

    public void logout(String token);
}
