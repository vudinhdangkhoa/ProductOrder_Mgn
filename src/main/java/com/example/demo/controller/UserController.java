package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.interf.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor   // Tự động tạo constructor với các field final
@Tag(name = "User", description = "API quản lý người dùng")
public class UserController {

  
    private final UserService userService;
  

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')") // Chỉ cho phép người dùng có quyền USER_VIEW truy cập
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @PageableDefault(size = 10) Pageable pageable) {

        ApiResponse<Page<UserResponse>> response
                = ApiResponse.<Page<UserResponse>>builder()
                        .success(true)
                        .message("Get all users successfully")
                        .data(userService.getAllUsers(pageable))
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')") // Chỉ cho phép người dùng có quyền USER_MANAGE truy cập
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody CreateUserRequest request) {
        
        userService.createUser(request);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("User created successfully")
                .build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')") // Chỉ cho phép người dùng có quyền USER_MANAGE truy cập
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("User updated successfully")
                .build());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('USER_ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<String>> updateUserRole(@PathVariable Long id, @RequestBody Long roleId) {
        
        userService.updateUserRole(id, roleId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("User role updated successfully")
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@RequestParam Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .data(userResponse)
                .build());
    }
    

}
