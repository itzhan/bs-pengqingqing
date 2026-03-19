package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("teaching_task")
public class TeachingTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private Long masterId;
    private Long apprenticeId;
    private Long heritageProjectId;
    private Long skillCategoryId;
    private LocalDateTime deadline;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    @TableField(exist = false)
    private String masterName;

    @TableField(exist = false)
    private String apprenticeName;

    @TableField(exist = false)
    private String projectName;

    @TableField(exist = false)
    private String skillCategoryName;
}
