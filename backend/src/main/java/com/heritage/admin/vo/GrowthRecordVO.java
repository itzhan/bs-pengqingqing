package com.heritage.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GrowthRecordVO {
    private Long id;
    private Long apprenticeId;
    private String recordType;
    private String title;
    private String description;
    private Long relatedId;
    private String relatedType;
    private LocalDateTime createdAt;

    // 关联字段
    private String apprenticeName;
}
