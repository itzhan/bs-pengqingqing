package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.dto.SkillCategoryDTO;
import com.heritage.admin.entity.SkillCategory;
import com.heritage.admin.vo.SkillCategoryVO;

import java.util.List;

public interface SkillCategoryService extends IService<SkillCategory> {
    List<SkillCategory> listAll();
    List<SkillCategoryVO> listTree();
    void createCategory(SkillCategoryDTO dto);
    void updateCategory(Long id, SkillCategoryDTO dto);
    void deleteCategory(Long id);
}
