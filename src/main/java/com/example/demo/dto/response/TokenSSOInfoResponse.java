package com.example.demo.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class TokenSSOInfoResponse {
    private boolean active;
    private Map<String, Object> userInfo;
}
