package com.heritage.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArtworkReviewDTO {
    @NotNull(message = "作品ID不能为空")
    private Long artworkId;
    @NotBlank(message = "点评内容不能为空")
    private String content;
    private Integer score;
}
