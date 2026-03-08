package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HeritageProjectDTO {
    @NotBlank(message = "项目名称不能为空")
    private String name;
    private Long categoryId;
    private String level;
    private String description;
    private String region;
    private String imageUrl;
}
