package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GrowthRecordCreateDTO {
    @NotBlank(message = "记录类型不能为空")
    private String recordType;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;
    private Long relatedId;
    private String relatedType;
}
