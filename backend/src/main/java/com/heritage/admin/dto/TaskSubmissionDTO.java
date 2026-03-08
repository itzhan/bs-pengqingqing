package com.heritage.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskSubmissionDTO {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    private String content;
    private String fileUrl;
}
