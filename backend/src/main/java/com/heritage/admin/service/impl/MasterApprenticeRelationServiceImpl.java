package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.RelationApplyDTO;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.HeritageProject;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.MasterProfile;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.HeritageProjectMapper;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.mapper.MasterProfileMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.service.MasterApprenticeRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterApprenticeRelationServiceImpl extends ServiceImpl<MasterApprenticeRelationMapper, MasterApprenticeRelation> implements MasterApprenticeRelationService {

    private final GrowthRecordMapper growthRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final MasterProfileMapper masterProfileMapper;
    private final HeritageProjectMapper heritageProjectMapper;

    @Override
    public void applyRelation(Long apprenticeId, RelationApplyDTO dto) {
        SysUser apprentice = requireUser(apprenticeId, "徒弟不存在");
        if (!isRole(apprentice, "APPRENTICE")) {
            throw new BusinessException("只有徒弟角色可以申请拜师");
        }
        if (Objects.equals(apprenticeId, dto.getMasterId())) {
            throw new BusinessException("不能向自己发起拜师申请");
        }

        // Validate master exists
        SysUser master = requireUser(dto.getMasterId(), "师傅不存在");
        if (!isRole(master, "MASTER")) {
            throw new BusinessException("目标用户不是师傅角色");
        }

        // Validate master has approved profile
        MasterProfile profile = masterProfileMapper.selectOne(
                new LambdaQueryWrapper<MasterProfile>()
                        .eq(MasterProfile::getUserId, dto.getMasterId())
                        .eq(MasterProfile::getAuditStatus, 1));
        if (profile == null) {
            throw new BusinessException("师傅档案未审核通过");
        }

        long activeCount = count(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .in(MasterApprenticeRelation::getStatus, 0, 1));
        if (activeCount > 0) {
            throw new BusinessException("你已有待处理或生效中的师徒关系，请先完成当前流程");
        }

        MasterApprenticeRelation relation = getOne(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, dto.getMasterId())
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .in(MasterApprenticeRelation::getStatus, 2, 3)
                .orderByDesc(MasterApprenticeRelation::getUpdatedAt)
                .last("LIMIT 1"));

        if (relation == null) {
            relation = new MasterApprenticeRelation();
            relation.setMasterId(dto.getMasterId());
            relation.setApprenticeId(apprenticeId);
        }

        relation.setHeritageProjectId(dto.getHeritageProjectId() != null ? dto.getHeritageProjectId() : profile.getHeritageProjectId());
        relation.setApplyReason(dto.getApplyReason());
        relation.setStatus(0);
        relation.setApplyTime(LocalDateTime.now());
        relation.setApproveTime(null);
        relation.setDissolveTime(null);
        relation.setDissolveReason(null);

        saveOrUpdate(relation);
    }

    @Override
    public void approveRelation(Long relationId, Long operatorId, boolean approved) {
        MasterApprenticeRelation relation = getById(relationId);
        if (relation == null) {
            throw new BusinessException("关系不存在");
        }

        SysUser operator = requireUser(operatorId, "操作用户不存在");
        boolean isAdmin = isRole(operator, "ADMIN");
        if (!relation.getMasterId().equals(operatorId) && !isAdmin) {
            throw new BusinessException("无权操作");
        }

        if (relation.getStatus() != 0) {
            throw new BusinessException("关系状态不正确");
        }

        if (approved) {
            long activeApproved = count(new LambdaQueryWrapper<MasterApprenticeRelation>()
                    .eq(MasterApprenticeRelation::getApprenticeId, relation.getApprenticeId())
                    .eq(MasterApprenticeRelation::getStatus, 1)
                    .ne(MasterApprenticeRelation::getId, relationId));
            if (activeApproved > 0) {
                throw new BusinessException("该徒弟已有生效中的师徒关系，无法重复通过");
            }

            relation.setStatus(1); // approved
            relation.setApproveTime(LocalDateTime.now());
            updateById(relation);

            // Create milestone growth record
            GrowthRecord record = new GrowthRecord();
            record.setApprenticeId(relation.getApprenticeId());
            record.setRecordType("MILESTONE");
            record.setTitle("成为" + displayName(requireUser(relation.getMasterId(), "师傅不存在"), "师傅") + "的徒弟");
            record.setDescription("师徒关系建立");
            record.setRelatedId(relationId);
            record.setRelatedType("RELATION");
            growthRecordMapper.insert(record);
        } else {
            removeConflictByStatus(relation, 2);
            relation.setStatus(2); // rejected
            relation.setApproveTime(LocalDateTime.now());
            updateById(relation);
        }
    }

    @Override
    public void cancelRelation(Long relationId, Long apprenticeId) {
        MasterApprenticeRelation relation = getById(relationId);
        if (relation == null) {
            throw new BusinessException("关系不存在");
        }
        if (!Objects.equals(relation.getApprenticeId(), apprenticeId)) {
            throw new BusinessException("无权操作");
        }
        if (relation.getStatus() != 0) {
            throw new BusinessException("只有待审核申请才可撤回");
        }

        removeConflictByStatus(relation, 3);
        relation.setStatus(3);
        relation.setDissolveReason("徒弟主动撤回申请");
        relation.setDissolveTime(LocalDateTime.now());
        updateById(relation);
    }

    @Override
    public void dissolveRelation(Long relationId, Long userId, String reason) {
        MasterApprenticeRelation relation = getById(relationId);
        if (relation == null) {
            throw new BusinessException("关系不存在");
        }
        if (relation.getStatus() != 1) {
            throw new BusinessException("只有已生效的师徒关系才可解除");
        }

        SysUser user = requireUser(userId, "用户不存在");
        if (!relation.getMasterId().equals(userId)
                && !relation.getApprenticeId().equals(userId)
                && !isRole(user, "ADMIN")) {
            throw new BusinessException("无权操作");
        }

        removeConflictByStatus(relation, 3);
        relation.setStatus(3); // dissolved
        relation.setDissolveTime(LocalDateTime.now());
        relation.setDissolveReason((reason == null || reason.isBlank()) ? "师徒关系已解除" : reason.trim());
        updateById(relation);
    }

    @Override
    public List<MasterApprenticeRelation> getByMasterId(Long masterId, Integer status, Long apprenticeId) {
        LambdaQueryWrapper<MasterApprenticeRelation> wrapper = new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, masterId);
        if (status != null) {
            wrapper.eq(MasterApprenticeRelation::getStatus, status);
        }
        if (apprenticeId != null) {
            wrapper.eq(MasterApprenticeRelation::getApprenticeId, apprenticeId);
        }
        wrapper.orderByDesc(MasterApprenticeRelation::getUpdatedAt);
        List<MasterApprenticeRelation> relations = list(wrapper);
        enrichRelations(relations);
        return relations;
    }

    @Override
    public List<MasterApprenticeRelation> getByApprenticeId(Long apprenticeId, Integer status, Long masterId) {
        LambdaQueryWrapper<MasterApprenticeRelation> wrapper = new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId);
        if (status != null) {
            wrapper.eq(MasterApprenticeRelation::getStatus, status);
        }
        if (masterId != null) {
            wrapper.eq(MasterApprenticeRelation::getMasterId, masterId);
        }
        wrapper.orderByDesc(MasterApprenticeRelation::getUpdatedAt);
        List<MasterApprenticeRelation> relations = list(wrapper);
        enrichRelations(relations);
        return relations;
    }

    @Override
    public PageResult<MasterApprenticeRelation> listRelations(int page, int size, Integer status, Long masterId, Long apprenticeId) {
        Page<MasterApprenticeRelation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MasterApprenticeRelation> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(MasterApprenticeRelation::getStatus, status);
        }
        if (masterId != null) {
            wrapper.eq(MasterApprenticeRelation::getMasterId, masterId);
        }
        if (apprenticeId != null) {
            wrapper.eq(MasterApprenticeRelation::getApprenticeId, apprenticeId);
        }

        wrapper.orderByDesc(MasterApprenticeRelation::getUpdatedAt);
        Page<MasterApprenticeRelation> result = page(pageParam, wrapper);
        enrichRelations(result.getRecords());
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    private void enrichRelations(List<MasterApprenticeRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return;
        }

        List<Long> userIds = relations.stream()
                .flatMap(relation -> java.util.stream.Stream.of(relation.getMasterId(), relation.getApprenticeId()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SysUser> userMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            userMap = sysUserMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, user -> user));
        }

        List<Long> projectIds = relations.stream()
                .map(MasterApprenticeRelation::getHeritageProjectId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<Long> masterIds = relations.stream()
                .map(MasterApprenticeRelation::getMasterId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, MasterProfile> profileMap = Collections.emptyMap();
        if (!masterIds.isEmpty()) {
            profileMap = masterProfileMapper.selectList(new LambdaQueryWrapper<MasterProfile>()
                            .in(MasterProfile::getUserId, masterIds)
                            .orderByDesc(MasterProfile::getUpdatedAt))
                    .stream()
                    .collect(Collectors.toMap(MasterProfile::getUserId, profile -> profile, (current, ignored) -> current));
            projectIds = java.util.stream.Stream.concat(
                            projectIds.stream(),
                            profileMap.values().stream()
                                    .map(MasterProfile::getHeritageProjectId)
                                    .filter(Objects::nonNull))
                    .distinct()
                    .collect(Collectors.toList());
        }
        Map<Long, HeritageProject> projectMap = Collections.emptyMap();
        if (!projectIds.isEmpty()) {
            projectMap = heritageProjectMapper.selectBatchIds(projectIds).stream()
                    .collect(Collectors.toMap(HeritageProject::getId, project -> project));
        }

        for (MasterApprenticeRelation relation : relations) {
            SysUser master = userMap.get(relation.getMasterId());
            if (master != null) {
                relation.setMasterName(displayName(master, "未命名师傅"));
                relation.setMasterAvatar(master.getAvatar());
            }
            SysUser apprentice = userMap.get(relation.getApprenticeId());
            if (apprentice != null) {
                relation.setApprenticeName(displayName(apprentice, "未命名徒弟"));
                relation.setApprenticeAvatar(apprentice.getAvatar());
            }
            Long projectId = relation.getHeritageProjectId();
            if (projectId == null) {
                MasterProfile profile = profileMap.get(relation.getMasterId());
                if (profile != null) {
                    projectId = profile.getHeritageProjectId();
                }
            }
            HeritageProject project = projectMap.get(projectId);
            if (project != null) {
                relation.setProjectName(project.getName());
            }
        }
    }

    private SysUser requireUser(Long userId, String message) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(message);
        }
        return user;
    }

    private void removeConflictByStatus(MasterApprenticeRelation relation, int targetStatus) {
        MasterApprenticeRelation conflict = getOne(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, relation.getMasterId())
                .eq(MasterApprenticeRelation::getApprenticeId, relation.getApprenticeId())
                .eq(MasterApprenticeRelation::getStatus, targetStatus)
                .ne(MasterApprenticeRelation::getId, relation.getId())
                .last("LIMIT 1"));
        if (conflict != null) {
            removeById(conflict.getId());
        }
    }

    private boolean isRole(SysUser user, String role) {
        return role.equalsIgnoreCase(user.getRole());
    }

    private String displayName(SysUser user, String fallback) {
        if (user == null) {
            return fallback;
        }
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName().trim();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername().trim();
        }
        return fallback;
    }
}
