package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.LearningMaterialDTO;
import com.heritage.admin.entity.LearningMaterial;
import com.heritage.admin.mapper.LearningMaterialMapper;
import com.heritage.admin.service.LearningMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningMaterialServiceImpl extends ServiceImpl<LearningMaterialMapper, LearningMaterial> implements LearningMaterialService {

    @Override
    public void createMaterial(Long uploaderId, LearningMaterialDTO dto) {
        LearningMaterial material = new LearningMaterial();
        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setFileUrl(dto.getFileUrl());
        material.setFileType(dto.getFileType());
        material.setFileSize(dto.getFileSize());
        material.setTaskId(dto.getTaskId());
        material.setUploaderId(uploaderId);
        material.setHeritageProjectId(dto.getHeritageProjectId());
        save(material);
    }

    @Override
    public void deleteMaterial(Long id, Long uploaderId) {
        LearningMaterial material = getById(id);
        if (material == null) {
            throw new BusinessException("材料不存在");
        }

        if (!material.getUploaderId().equals(uploaderId)) {
            throw new BusinessException("无权操作");
        }

        removeById(id);
    }

    @Override
    public PageResult<LearningMaterial> listByTask(Long taskId, int page, int size) {
        Page<LearningMaterial> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LearningMaterial> wrapper = new LambdaQueryWrapper<LearningMaterial>()
                .eq(LearningMaterial::getTaskId, taskId)
                .orderByDesc(LearningMaterial::getCreatedAt);
        Page<LearningMaterial> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<LearningMaterial> listByUploader(Long uploaderId, int page, int size) {
        Page<LearningMaterial> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LearningMaterial> wrapper = new LambdaQueryWrapper<LearningMaterial>()
                .eq(LearningMaterial::getUploaderId, uploaderId)
                .orderByDesc(LearningMaterial::getCreatedAt);
        Page<LearningMaterial> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<LearningMaterial> listAll(int page, int size) {
        Page<LearningMaterial> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LearningMaterial> wrapper = new LambdaQueryWrapper<LearningMaterial>()
                .orderByDesc(LearningMaterial::getCreatedAt);
        Page<LearningMaterial> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
