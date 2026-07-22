package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.demo.dto.response.RoleResponse;
import com.example.demo.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    

   
    RoleResponse toResponse(Role role);
    List<RoleResponse> toResponseList(List<Role> roles);

}
