package com.heritage.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArtworkVO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private String apprenticeName;
    private String apprenticeAvatar;
    private String projectName;
    private String projectImageUrl;
    private String firstImageUrl;
}
