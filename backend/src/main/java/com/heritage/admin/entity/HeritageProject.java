package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("heritage_project")
public class HeritageProject {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Long categoryId;
    private String level;
    private String description;
    private String region;
    private String imageUrl;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    @TableField(exist = false)
    private String categoryName;
}
