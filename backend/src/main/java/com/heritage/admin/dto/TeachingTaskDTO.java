package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeachingTaskDTO {
    @NotBlank(message = "任务标题不能为空")
    private String title;
    private String description;
    private Long apprenticeId;
    private Long heritageProjectId;
    private Long skillCategoryId;
    private LocalDateTime deadline;
}
