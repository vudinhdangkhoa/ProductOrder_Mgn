package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.RoleResponse;
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
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> listUsers(
            @PageableDefault(size = 10) Pageable pageable) {

        ApiResponse<PageResponse<UserResponse>> response
                = ApiResponse.<PageResponse<UserResponse>>builder()
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

    @PutMapping("/{id}/{roleId}")
    @PreAuthorize("hasAuthority('USER_ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<String>> updateUserRole(@PathVariable Long id, @PathVariable Long roleId) {
        
        userService.updateUserRole(id, roleId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("User role updated successfully")
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@RequestParam(required = false) Optional<Long> userId, @RequestParam(required = false) Optional<String> accessToken, Authentication authentication) {
        
       UserResponse userResponse;
       if(userId.isPresent()){
          userResponse = userService.getUserById(userId.get());
       }else{

       String username = authentication != null ? authentication.getName() : null;
        userResponse = userService.getUserByName(username);
       }
        

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .data(userResponse)
                .build());
    }
    
    @GetMapping("list-operator")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllOperatorUsersList() {
        List<UserResponse> users = userService.getAllOperatorUsersWithoutPagination();
        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .data(users)
                .message("Get all operator users successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @GetMapping("list")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsersList() {
        List<UserResponse> users = userService.getAllUsersWithoutPagination();
        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .data(users)
                .message("Get all users successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @GetMapping("roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRolesList() {
        List<RoleResponse> roles = userService.getAllRolesWithoutPagination();
        return ResponseEntity.ok(ApiResponse.<List<RoleResponse>>builder()
                .success(true)
                .data(roles)
                .message("Get all roles successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/lock/{id}")
    public ResponseEntity<ApiResponse<String>> LockUser(@PathVariable Long id) {
        //TODO: process PUT request
        
        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Lock User  successfully")
                .build());
    }

    @PutMapping("unlock/{id}")
    public ResponseEntity<ApiResponse<String>> unlockUser(@PathVariable Long id) {
        //TODO: process PUT request
        
        userService.unlockUser(id); 

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Unlock User  successfully")
                .build());
    }
}
