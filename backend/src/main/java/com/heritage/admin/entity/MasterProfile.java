package com.heritage.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("master_profile")
public class MasterProfile {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private Long heritageProjectId;
    private Long skillCategoryId;
    private Integer careerYears;
    private String careerHistory;
    private String specialties;
    private String representativeWorks;
    private String bio;
    private String honor;
    private Integer auditStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String avatar;

    @TableField(exist = false)
    private String projectName;

    @TableField(exist = false)
    private String skillCategoryName;
}
