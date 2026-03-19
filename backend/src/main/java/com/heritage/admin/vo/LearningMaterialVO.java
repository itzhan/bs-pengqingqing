package com.heritage.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningMaterialVO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long taskId;
    private Long uploaderId;
    private Long heritageProjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private String uploaderName;
}
