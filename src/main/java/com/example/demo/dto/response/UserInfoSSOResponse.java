package com.example.demo.dto.response;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoSSOResponse {
private String sub;
        private String email;
        private String fullName;
        private String userName;
        private String department;
        private String position;
        private String avatar;
        private String telegram;
        private Optional<String> roleName;
}
