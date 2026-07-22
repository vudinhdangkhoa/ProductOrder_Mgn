package com.example.demo.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.interf.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleMapper roleMapper;

    @Override
    public UserResponse getUserByName(String name) {
        return userRepository.findByUsernameAndIsDeletedFalse(name)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại", 0));
    }

    @Override
    public UserResponse getUserById(Long id) {
        
        return userRepository.findByIdAndIsDeletedFalse(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại", id));

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {

        return PageResponse.fromPage(userRepository.findAll(pageable)
                .map(userMapper::toResponse));
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        
        try{

            //kiểm tra email và username đã tồn tại chưa
            if(userRepository.existsByEmail(request.getEmail())){
                throw new DuplicateResourceException("Email đã tồn tại: " + request.getUsername());
            }

            //fetch role từ DB
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role không tồn tại: " + request.getRoleId(), request.getRoleId()));

            //tạo user entity từ request
            User user = userMapper.toEntity(request);

            //gán role cho user
            user.setRole(role);

           

            //lưu user vào DB
            return userMapper.toResponse(userRepository.save(user));


        }
        catch(DuplicateResourceException | ResourceNotFoundException e){
            throw e;
        }
        
        catch(Exception e){
            log.error("Lỗi khi tạo người dùng: ", e);
            throw new RuntimeException("Lỗi khi tạo người dùng: " + e.getMessage());



        }
        

    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

       try{

            //tìm user theo id
            User user = userRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại", id));

            //cập nhật thông tin từ request
            userMapper.updateEntityFromRequest(request, user);

            //lưu thay đổi vào DB
            return userMapper.toResponse(userRepository.save(user));
        

       }
       catch(ResourceNotFoundException e){
            throw e;
        }
        catch(Exception e){
            log.error("Lỗi khi cập nhật người dùng: ", e);
            throw new RuntimeException("Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        
        userRepository.softDeleteById(id);
        
        return ;
    }

    @Override
    @Transactional
    public void unlockUser(Long id) {
        User user = userRepository.findByIdAndIsDeletedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại", id));

        user.setIsDeleted(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Long roleId) {
        
        // Kiểm tra xem người dùng có tồn tại và chưa bị xóa
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại", roleId));

        //kiểm tra xem role có tồn tại không
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role không tồn tại", roleId));

        // Cập nhật role cho người dùng
        userRepository.updateUserRole(userId, roleId);
        return;

    }

    @Override
    @Transactional
    public UserResponse getUserByUsernameAndIsDeletedFalse(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại", 0));
    }

    @Override
    public List<UserResponse> getAllOperatorUsersWithoutPagination() {

        Role role = roleRepository.findByNameRole(UserRole.OPERATOR)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER không tồn tại", 0));

        return userMapper.toResponseList(userRepository.findAllByIsDeletedFalseAndRole_Id(role.getId()));
    }

    @Override
    public List<UserResponse> getAllUsersWithoutPagination() {


        return userMapper.toResponseList(userRepository.findAllByIsDeletedFalse());
    }

    @Override
    public List<RoleResponse> getAllRolesWithoutPagination() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toResponseList(roles);
    }

}
