package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("master_apprentice_relation")
public class MasterApprenticeRelation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long masterId;
    private Long apprenticeId;
    private Long heritageProjectId;
    private String applyReason;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime dissolveTime;
    private String dissolveReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String masterName;

    @TableField(exist = false)
    private String masterAvatar;

    @TableField(exist = false)
    private String apprenticeName;

    @TableField(exist = false)
    private String apprenticeAvatar;

    @TableField(exist = false)
    private String projectName;
}
