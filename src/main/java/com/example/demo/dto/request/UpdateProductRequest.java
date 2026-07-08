package com.example.demo.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 100, message = "Tên sản phẩm tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;

    @NotBlank(message = "Trạng thái không được để trống")
    private Boolean isActive;
}
