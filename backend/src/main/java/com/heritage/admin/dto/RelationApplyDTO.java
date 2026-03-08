package com.heritage.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RelationApplyDTO {
    @NotNull(message = "师傅ID不能为空")
    private Long masterId;
    private Long heritageProjectId;
    private String applyReason;
}
