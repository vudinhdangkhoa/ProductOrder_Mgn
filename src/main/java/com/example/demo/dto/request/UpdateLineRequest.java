package com.example.demo.dto.request;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLineRequest {

    @Size(max = 50, message = "Tên dây chuyền tối đa 50 ký tự")
    private String lineName;

    @Size(max = 100, message = "Mô tả tối đa 100 ký tự")
    private String description;

}
