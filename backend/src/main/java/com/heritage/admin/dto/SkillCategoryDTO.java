package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillCategoryDTO {
    @NotBlank(message = "类别名称不能为空")
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private String description;
}
