package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.RelationApplyDTO;
import com.heritage.admin.entity.MasterApprenticeRelation;

import java.util.List;

public interface MasterApprenticeRelationService extends IService<MasterApprenticeRelation> {
    void applyRelation(Long apprenticeId, RelationApplyDTO dto);
    void approveRelation(Long relationId, Long masterId, boolean approved);
    void cancelRelation(Long relationId, Long apprenticeId);
    void dissolveRelation(Long relationId, Long userId, String reason);
    List<MasterApprenticeRelation> getByMasterId(Long masterId, Integer status, Long apprenticeId);
    List<MasterApprenticeRelation> getByApprenticeId(Long apprenticeId, Integer status, Long masterId);
    PageResult<MasterApprenticeRelation> listRelations(int page, int size, Integer status, Long masterId, Long apprenticeId);
}
