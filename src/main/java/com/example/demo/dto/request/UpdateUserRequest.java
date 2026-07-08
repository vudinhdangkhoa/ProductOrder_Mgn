package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(max = 100, message = "Tên người dùng tối đa 100 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "trạng thái hoạt động không được để trống")
    private Boolean isActive;
}
