package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.LearningMaterialDTO;
import com.heritage.admin.entity.LearningMaterial;

public interface LearningMaterialService extends IService<LearningMaterial> {
    void createMaterial(Long uploaderId, LearningMaterialDTO dto);
    void deleteMaterial(Long id, Long uploaderId);
    PageResult<LearningMaterial> listByTask(Long taskId, int page, int size);
    PageResult<LearningMaterial> listByUploader(Long uploaderId, int page, int size);
    PageResult<LearningMaterial> listAll(int page, int size);
}
