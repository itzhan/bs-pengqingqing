package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("learning_material")
public class LearningMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long taskId;
    private Long uploaderId;
    private Long heritageProjectId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
