package com.example.demo.service.interf;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.dto.response.UserResponse;

public interface UserService {

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    void unlockUser(Long id);

    void updateUserRole(Long userId, Long roleId);

    UserResponse getUserByName(String name);

    UserResponse getUserByUsernameAndIsDeletedFalse(String username);

    List<UserResponse> getAllUsersWithoutPagination();

    List<UserResponse> getAllOperatorUsersWithoutPagination();

    List<RoleResponse> getAllRolesWithoutPagination();
}
