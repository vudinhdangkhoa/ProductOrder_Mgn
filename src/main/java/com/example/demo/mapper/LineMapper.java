package com.example.demo.mapper;

import com.example.demo.dto.request.CreateLineRequest;
import com.example.demo.dto.request.UpdateLineRequest;
import com.example.demo.dto.response.LineResponse;
import com.example.demo.entity.Line;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LineMapper {

    LineResponse toResponse(Line line);

    List<LineResponse> toResponseList(List<Line> lines);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "isActive", ignore = true) // Field này mặc định là true từ Entity
    Line toEntity(CreateLineRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lineCode", ignore = true) // Cấm ghi đè mã dây chuyền khi update
    void updateEntityFromRequest(UpdateLineRequest req, @MappingTarget Line line);
}