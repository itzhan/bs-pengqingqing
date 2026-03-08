package com.heritage.admin.vo;

import lombok.Data;
import java.util.List;

@Data
public class SkillCategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private String description;
    private List<SkillCategoryVO> children;
}
