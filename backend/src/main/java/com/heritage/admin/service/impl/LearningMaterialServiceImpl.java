package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.LearningMaterialDTO;
import com.heritage.admin.entity.LearningMaterial;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.LearningMaterialMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.service.LearningMaterialService;
import com.heritage.admin.vo.LearningMaterialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningMaterialServiceImpl extends ServiceImpl<LearningMaterialMapper, LearningMaterial> implements LearningMaterialService {

    private final SysUserMapper sysUserMapper;

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
    public void updateMaterial(Long id, Long operatorId, LearningMaterialDTO dto) {
        LearningMaterial material = getById(id);
        if (material == null) {
            throw new BusinessException("材料不存在");
        }

        SysUser operator = sysUserMapper.selectById(operatorId);
        if (operator == null) {
            throw new BusinessException("用户不存在");
        }
        boolean isAdmin = "ADMIN".equalsIgnoreCase(operator.getRole());
        if (!isAdmin && !material.getUploaderId().equals(operatorId)) {
            throw new BusinessException("无权操作");
        }

        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setFileUrl(dto.getFileUrl());
        material.setFileType(dto.getFileType());
        material.setFileSize(dto.getFileSize());
        material.setTaskId(dto.getTaskId());
        material.setHeritageProjectId(dto.getHeritageProjectId());
        updateById(material);
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
    public PageResult<LearningMaterialVO> listAll(int page, int size) {
        Page<LearningMaterial> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LearningMaterial> wrapper = new LambdaQueryWrapper<LearningMaterial>()
                .orderByDesc(LearningMaterial::getCreatedAt);
        Page<LearningMaterial> result = page(pageParam, wrapper);

        // 批量查询上传者姓名
        List<Long> uploaderIds = result.getRecords().stream()
                .map(LearningMaterial::getUploaderId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> userNameMap = java.util.Collections.emptyMap();
        if (!uploaderIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(uploaderIds);
            userNameMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
        }

        Map<Long, String> finalUserNameMap = userNameMap;
        List<LearningMaterialVO> voList = result.getRecords().stream().map(m -> {
            LearningMaterialVO vo = new LearningMaterialVO();
            BeanUtils.copyProperties(m, vo);
            if (m.getUploaderId() != null) {
                vo.setUploaderName(finalUserNameMap.getOrDefault(m.getUploaderId(), "未知用户"));
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }
}
