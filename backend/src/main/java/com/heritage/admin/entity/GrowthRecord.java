package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("growth_record")
public class GrowthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long apprenticeId;
    private String recordType;
    private String title;
    private String description;
    private Long relatedId;
    private String relatedType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
