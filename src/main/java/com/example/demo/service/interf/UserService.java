package com.example.demo.service.interf;

import com.example.demo.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
}
