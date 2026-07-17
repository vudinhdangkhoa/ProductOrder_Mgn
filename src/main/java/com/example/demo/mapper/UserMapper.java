package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.UserInfoSSOResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

   @Mapping(source = "role.nameRole", target = "roleName")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
  
    @Mapping(target = "role", ignore = true) // Role sẽ được fetch từ DB ở Service
    User toEntity(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
  
    @Mapping(target = "role", ignore = true)
    User toEntityFromSSO(UserInfoSSOResponse userResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
   
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
  
      @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest req, @MappingTarget User user);

}
