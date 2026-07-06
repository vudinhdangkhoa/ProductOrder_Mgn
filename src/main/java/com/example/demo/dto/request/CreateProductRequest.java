package com.example.demo.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max=255,message = "name khong duoc qua 255 ky tu")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max=255,message = "description khong duoc qua 255 ky tu")
    private String description;

    @NotBlank(message = "category is required")
    private Long categoryId;
}
