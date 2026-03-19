package com.heritage.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RelationApplyDTO {
    @NotNull(message = "师傅ID不能为空")
    private Long masterId;
    private Long heritageProjectId;
    @NotBlank(message = "申请理由不能为空")
    private String applyReason;
}
