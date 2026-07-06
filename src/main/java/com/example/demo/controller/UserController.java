package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor   // Tự động tạo constructor với các field final
public class UserController {

    private final UserMapper userMapper;

    @GetMapping
    public String listUsers() {
        return "User endpoint is ready";
    }

    @PostMapping("")
    public UserResponse postMethodName(@RequestBody CreateUserRequest entity) {
       
       User newUser = userMapper.toEntity(entity);
        
        return userMapper.toResponse(newUser);
    }
    
}
