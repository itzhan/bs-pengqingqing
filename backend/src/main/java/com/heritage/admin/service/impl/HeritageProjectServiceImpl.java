package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.HeritageProjectDTO;
import com.heritage.admin.entity.HeritageProject;
import com.heritage.admin.mapper.HeritageProjectMapper;
import com.heritage.admin.service.HeritageProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeritageProjectServiceImpl extends ServiceImpl<HeritageProjectMapper, HeritageProject> implements HeritageProjectService {

    @Override
    public PageResult<HeritageProject> listProjects(int page, int size, String keyword, Long categoryId, String level) {
        Page<HeritageProject> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HeritageProject> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(HeritageProject::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(HeritageProject::getCategoryId, categoryId);
        }
        if (level != null && !level.isEmpty()) {
            wrapper.eq(HeritageProject::getLevel, level);
        }

        wrapper.orderByDesc(HeritageProject::getCreatedAt);
        Page<HeritageProject> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<HeritageProject> listAll() {
        return list(new LambdaQueryWrapper<HeritageProject>()
                .eq(HeritageProject::getStatus, 1)
                .orderByDesc(HeritageProject::getCreatedAt));
    }

    @Override
    public void createProject(HeritageProjectDTO dto) {
        HeritageProject project = new HeritageProject();
        project.setName(dto.getName());
        project.setCategoryId(dto.getCategoryId());
        project.setLevel(dto.getLevel());
        project.setDescription(dto.getDescription());
        project.setRegion(dto.getRegion());
        project.setImageUrl(dto.getImageUrl());
        project.setStatus(1);
        save(project);
    }

    @Override
    public void updateProject(Long id, HeritageProjectDTO dto) {
        HeritageProject project = getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        project.setName(dto.getName());
        project.setCategoryId(dto.getCategoryId());
        project.setLevel(dto.getLevel());
        project.setDescription(dto.getDescription());
        project.setRegion(dto.getRegion());
        project.setImageUrl(dto.getImageUrl());
        updateById(project);
    }

    @Override
    public void deleteProject(Long id) {
        HeritageProject project = getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        removeById(id);
    }
}
