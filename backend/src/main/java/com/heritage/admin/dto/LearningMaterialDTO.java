package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LearningMaterialDTO {
    @NotBlank(message = "材料标题不能为空")
    private String title;
    private String description;
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long taskId;
    private Long heritageProjectId;
}
