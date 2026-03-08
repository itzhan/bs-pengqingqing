package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("artwork_review")
public class ArtworkReview {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long artworkId;
    private Long reviewerId;
    private String content;
    private Integer score;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
