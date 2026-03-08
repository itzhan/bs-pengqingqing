package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.RelationApplyDTO;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.MasterProfile;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.mapper.MasterProfileMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.service.MasterApprenticeRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterApprenticeRelationServiceImpl extends ServiceImpl<MasterApprenticeRelationMapper, MasterApprenticeRelation> implements MasterApprenticeRelationService {

    private final GrowthRecordMapper growthRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final MasterProfileMapper masterProfileMapper;

    @Override
    public void applyRelation(Long apprenticeId, RelationApplyDTO dto) {
        // Validate master exists
        SysUser master = sysUserMapper.selectById(dto.getMasterId());
        if (master == null) {
            throw new BusinessException("师傅不存在");
        }

        // Validate master has approved profile
        MasterProfile profile = masterProfileMapper.selectOne(
                new LambdaQueryWrapper<MasterProfile>()
                        .eq(MasterProfile::getUserId, dto.getMasterId())
                        .eq(MasterProfile::getAuditStatus, 1));
        if (profile == null) {
            throw new BusinessException("师傅档案未审核通过");
        }

        // Check no existing active relation
        long existingCount = count(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, dto.getMasterId())
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .in(MasterApprenticeRelation::getStatus, 0, 1)); // 0=pending, 1=approved
        if (existingCount > 0) {
            throw new BusinessException("已存在进行中的师徒关系");
        }

        MasterApprenticeRelation relation = new MasterApprenticeRelation();
        relation.setMasterId(dto.getMasterId());
        relation.setApprenticeId(apprenticeId);
        relation.setHeritageProjectId(dto.getHeritageProjectId());
        relation.setApplyReason(dto.getApplyReason());
        relation.setStatus(0); // pending
        relation.setApplyTime(LocalDateTime.now());
        save(relation);
    }

    @Override
    public void approveRelation(Long relationId, Long masterId, boolean approved) {
        MasterApprenticeRelation relation = getById(relationId);
        if (relation == null) {
            throw new BusinessException("关系不存在");
        }

        if (!relation.getMasterId().equals(masterId)) {
            throw new BusinessException("无权操作");
        }

        if (relation.getStatus() != 0) {
            throw new BusinessException("关系状态不正确");
        }

        if (approved) {
            relation.setStatus(1); // approved
            relation.setApproveTime(LocalDateTime.now());
            updateById(relation);

            // Create milestone growth record
            GrowthRecord record = new GrowthRecord();
            record.setApprenticeId(relation.getApprenticeId());
            record.setRecordType("MILESTONE");
            record.setTitle("成为" + sysUserMapper.selectById(masterId).getRealName() + "的徒弟");
            record.setDescription("师徒关系建立");
            record.setRelatedId(relationId);
            record.setRelatedType("RELATION");
            growthRecordMapper.insert(record);
        } else {
            relation.setStatus(2); // rejected
            relation.setApproveTime(LocalDateTime.now());
            updateById(relation);
        }
    }

    @Override
    public void dissolveRelation(Long relationId, Long userId, String reason) {
        MasterApprenticeRelation relation = getById(relationId);
        if (relation == null) {
            throw new BusinessException("关系不存在");
        }

        // Check permission: master or admin
        SysUser user = sysUserMapper.selectById(userId);
        if (!relation.getMasterId().equals(userId) && !"ADMIN".equals(user.getRole())) {
            throw new BusinessException("无权操作");
        }

        relation.setStatus(3); // dissolved
        relation.setDissolveTime(LocalDateTime.now());
        relation.setDissolveReason(reason);
        updateById(relation);
    }

    @Override
    public List<MasterApprenticeRelation> getByMasterId(Long masterId) {
        return list(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, masterId)
                .eq(MasterApprenticeRelation::getStatus, 1)
                .orderByDesc(MasterApprenticeRelation::getCreatedAt));
    }

    @Override
    public List<MasterApprenticeRelation> getByApprenticeId(Long apprenticeId) {
        return list(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .eq(MasterApprenticeRelation::getStatus, 1)
                .orderByDesc(MasterApprenticeRelation::getCreatedAt));
    }

    @Override
    public PageResult<MasterApprenticeRelation> listRelations(int page, int size, Integer status) {
        Page<MasterApprenticeRelation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MasterApprenticeRelation> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(MasterApprenticeRelation::getStatus, status);
        }

        wrapper.orderByDesc(MasterApprenticeRelation::getCreatedAt);
        Page<MasterApprenticeRelation> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
