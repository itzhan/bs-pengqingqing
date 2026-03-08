package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("artwork")
public class Artwork {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private Long apprenticeId;
    private Long masterId;
    private Long heritageProjectId;
    private Long skillCategoryId;
    private String imageUrls;
    private String videoUrl;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
