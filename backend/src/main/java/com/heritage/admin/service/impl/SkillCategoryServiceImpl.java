package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.dto.SkillCategoryDTO;
import com.heritage.admin.entity.SkillCategory;
import com.heritage.admin.mapper.SkillCategoryMapper;
import com.heritage.admin.service.SkillCategoryService;
import com.heritage.admin.vo.SkillCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillCategoryServiceImpl extends ServiceImpl<SkillCategoryMapper, SkillCategory> implements SkillCategoryService {

    @Override
    public List<SkillCategory> listAll() {
        return list(new LambdaQueryWrapper<SkillCategory>()
                .orderByAsc(SkillCategory::getSortOrder)
                .orderByAsc(SkillCategory::getId));
    }

    @Override
    public List<SkillCategoryVO> listTree() {
        List<SkillCategory> all = listAll();
        // 建立 id -> name 映射
        java.util.Map<Long, String> idNameMap = all.stream()
                .collect(Collectors.toMap(SkillCategory::getId, SkillCategory::getName));

        List<SkillCategoryVO> voList = all.stream().map(cat -> {
            SkillCategoryVO vo = new SkillCategoryVO();
            BeanUtils.copyProperties(cat, vo);
            // 填充父级名称
            if (cat.getParentId() != null && cat.getParentId() > 0) {
                vo.setParentName(idNameMap.getOrDefault(cat.getParentId(), "未知"));
            }
            return vo;
        }).collect(Collectors.toList());

        List<SkillCategoryVO> roots = new ArrayList<>();
        for (SkillCategoryVO vo : voList) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                roots.add(vo);
            }
        }
        for (SkillCategoryVO root : roots) {
            root.setChildren(findChildren(root.getId(), voList));
        }
        return roots;
    }

    private List<SkillCategoryVO> findChildren(Long parentId, List<SkillCategoryVO> allList) {
        List<SkillCategoryVO> children = allList.stream()
                .filter(vo -> parentId.equals(vo.getParentId()))
                .collect(Collectors.toList());
        for (SkillCategoryVO child : children) {
            child.setChildren(findChildren(child.getId(), allList));
        }
        return children;
    }

    @Override
    public void createCategory(SkillCategoryDTO dto) {
        SkillCategory category = new SkillCategory();
        category.setName(dto.getName());
        category.setParentId(dto.getParentId());
        category.setSortOrder(dto.getSortOrder());
        category.setDescription(dto.getDescription());
        save(category);
    }

    @Override
    public void updateCategory(Long id, SkillCategoryDTO dto) {
        SkillCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("技艺类别不存在");
        }
        category.setName(dto.getName());
        category.setParentId(dto.getParentId());
        category.setSortOrder(dto.getSortOrder());
        category.setDescription(dto.getDescription());
        updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        SkillCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("技艺类别不存在");
        }
        long childCount = count(new LambdaQueryWrapper<SkillCategory>()
                .eq(SkillCategory::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子类别，不能删除");
        }
        removeById(id);
    }
}
