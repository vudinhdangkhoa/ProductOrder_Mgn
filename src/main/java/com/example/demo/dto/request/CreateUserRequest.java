package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message= "mail khong hop le")
    @Size(max=255,message = "email khong duoc qua 255 ky tu")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=6,max=255,message = "password phai tu 6 den 255 ky tu")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max=255,message = "full name khong duoc qua 255 ky tu")
    private String fullName;

    @NotBlank(message = "role is required")
    private Long roleId;

    @NotBlank(message = "Username is required")
    @Size(max=255,message = "username khong duoc qua 255 ky tu")
    private String username;
}
