package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_submission")
public class TaskSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;
    private Long apprenticeId;
    private String content;
    private String fileUrl;
    private Integer status;
    private String masterComment;
    private Integer score;
    private LocalDateTime submitTime;
    private LocalDateTime reviewTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String apprenticeName;
}
