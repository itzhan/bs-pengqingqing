package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.LearningMaterialDTO;
import com.heritage.admin.entity.LearningMaterial;
import com.heritage.admin.vo.LearningMaterialVO;

public interface LearningMaterialService extends IService<LearningMaterial> {
    void createMaterial(Long uploaderId, LearningMaterialDTO dto);
    void updateMaterial(Long id, Long operatorId, LearningMaterialDTO dto);
    void deleteMaterial(Long id, Long uploaderId);
    PageResult<LearningMaterial> listByTask(Long taskId, int page, int size);
    PageResult<LearningMaterial> listByUploader(Long uploaderId, int page, int size);
    PageResult<LearningMaterialVO> listAll(int page, int size);
}
