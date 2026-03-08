package com.heritage.admin.dto;

import lombok.Data;

@Data
public class MasterProfileDTO {
    private String title;
    private Long heritageProjectId;
    private Long skillCategoryId;
    private Integer careerYears;
    private String careerHistory;
    private String specialties;
    private String representativeWorks;
    private String bio;
    private String honor;
}
