package com.example.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String userName;
    private String fullName;
    private String email;
    private String roleName; 
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String department;
    private String position;
    private String sub;
   
    // KHONG BAO GIO tra ve: password, token, internal fields
}
