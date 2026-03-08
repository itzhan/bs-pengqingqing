package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArtworkDTO {
    @NotBlank(message = "作品标题不能为空")
    private String title;
    private String description;
    private Long heritageProjectId;
    private Long skillCategoryId;
    private String imageUrls;
    private String videoUrl;
}
