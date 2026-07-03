package com.example.demo.service.impl;


import org.springframework.stereotype.Service;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.interf.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getUserById(Long id) {
       return null;
    }
}
