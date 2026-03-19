package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.TeachingTaskDTO;
import com.heritage.admin.entity.HeritageProject;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.SkillCategory;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.entity.TeachingTask;
import com.heritage.admin.mapper.HeritageProjectMapper;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.mapper.SkillCategoryMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.mapper.TeachingTaskMapper;
import com.heritage.admin.service.TeachingTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachingTaskServiceImpl extends ServiceImpl<TeachingTaskMapper, TeachingTask> implements TeachingTaskService {

    private final MasterApprenticeRelationMapper masterApprenticeRelationMapper;
    private final SysUserMapper sysUserMapper;
    private final HeritageProjectMapper heritageProjectMapper;
    private final SkillCategoryMapper skillCategoryMapper;

    @Override
    public void createTask(Long masterId, TeachingTaskDTO dto) {
        validateTaskTarget(masterId, dto.getApprenticeId());

        TeachingTask task = new TeachingTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setMasterId(masterId);
        task.setApprenticeId(dto.getApprenticeId());
        task.setHeritageProjectId(dto.getHeritageProjectId());
        task.setSkillCategoryId(dto.getSkillCategoryId());
        task.setDeadline(dto.getDeadline());
        task.setStatus(0); // pending
        save(task);
    }

    @Override
    public void updateTask(Long taskId, Long masterId, TeachingTaskDTO dto) {
        TeachingTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!task.getMasterId().equals(masterId)) {
            throw new BusinessException("无权操作");
        }

        validateTaskTarget(masterId, dto.getApprenticeId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setApprenticeId(dto.getApprenticeId());
        task.setHeritageProjectId(dto.getHeritageProjectId());
        task.setSkillCategoryId(dto.getSkillCategoryId());
        task.setDeadline(dto.getDeadline());
        updateById(task);
    }

    @Override
    public void updateTaskStatus(Long taskId, Long masterId, Integer status) {
        TeachingTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!task.getMasterId().equals(masterId)) {
            throw new BusinessException("无权操作");
        }

        task.setStatus(status);
        updateById(task);
    }

    @Override
    public PageResult<TeachingTask> listByMaster(Long masterId, int page, int size, Long apprenticeId) {
        Page<TeachingTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TeachingTask> wrapper = new LambdaQueryWrapper<TeachingTask>()
                .eq(TeachingTask::getMasterId, masterId);
        if (apprenticeId != null) {
            wrapper.eq(TeachingTask::getApprenticeId, apprenticeId);
        }
        wrapper.orderByDesc(TeachingTask::getCreatedAt);
        Page<TeachingTask> result = page(pageParam, wrapper);
        enrichTasks(result.getRecords());
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<TeachingTask> listByApprentice(Long apprenticeId, int page, int size) {
        // Get apprentice's approved master IDs
        List<MasterApprenticeRelation> relations = masterApprenticeRelationMapper.selectList(
                new LambdaQueryWrapper<MasterApprenticeRelation>()
                        .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                        .eq(MasterApprenticeRelation::getStatus, 1));
        List<Long> masterIds = relations.stream()
                .map(MasterApprenticeRelation::getMasterId)
                .collect(Collectors.toList());

        if (masterIds.isEmpty()) {
            return new PageResult<>(List.of(), 0, page, size);
        }

        Page<TeachingTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TeachingTask> wrapper = new LambdaQueryWrapper<TeachingTask>()
                .in(TeachingTask::getMasterId, masterIds)
                .and(w -> w.eq(TeachingTask::getApprenticeId, apprenticeId)
                        .or().isNull(TeachingTask::getApprenticeId))
                .orderByDesc(TeachingTask::getCreatedAt);
        Page<TeachingTask> result = page(pageParam, wrapper);
        enrichTasks(result.getRecords());
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<TeachingTask> listAll(int page, int size, Integer status) {
        Page<TeachingTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TeachingTask> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(TeachingTask::getStatus, status);
        }

        wrapper.orderByDesc(TeachingTask::getCreatedAt);
        Page<TeachingTask> result = page(pageParam, wrapper);
        enrichTasks(result.getRecords());
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public TeachingTask getTaskDetail(Long taskId, Long userId) {
        TeachingTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        boolean isOwnerMaster = Objects.equals(task.getMasterId(), userId);
        boolean isAssignedApprentice = Objects.equals(task.getApprenticeId(), userId);
        boolean canAccessCommonTask = task.getApprenticeId() == null && hasApprovedRelation(task.getMasterId(), userId);

        if (!isAdmin && !isOwnerMaster && !isAssignedApprentice && !canAccessCommonTask) {
            throw new BusinessException("无权查看该任务");
        }

        enrichTasks(List.of(task));
        return task;
    }

    private void validateTaskTarget(Long masterId, Long apprenticeId) {
        if (apprenticeId == null) {
            return;
        }
        if (!hasApprovedRelation(masterId, apprenticeId)) {
            throw new BusinessException("只能给已绑定的徒弟布置任务");
        }
    }

    private boolean hasApprovedRelation(Long masterId, Long apprenticeId) {
        return masterApprenticeRelationMapper.selectCount(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, masterId)
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .eq(MasterApprenticeRelation::getStatus, 1)) > 0;
    }

    private void enrichTasks(List<TeachingTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        List<Long> userIds = tasks.stream()
                .flatMap(task -> java.util.stream.Stream.of(task.getMasterId(), task.getApprenticeId()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> userMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            userMap = sysUserMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, user -> user));
        }

        List<Long> projectIds = tasks.stream()
                .map(TeachingTask::getHeritageProjectId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, HeritageProject> projectMap = Collections.emptyMap();
        if (!projectIds.isEmpty()) {
            projectMap = heritageProjectMapper.selectBatchIds(projectIds).stream()
                    .collect(Collectors.toMap(HeritageProject::getId, project -> project));
        }

        List<Long> skillCategoryIds = tasks.stream()
                .map(TeachingTask::getSkillCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SkillCategory> skillCategoryMap = Collections.emptyMap();
        if (!skillCategoryIds.isEmpty()) {
            skillCategoryMap = skillCategoryMapper.selectBatchIds(skillCategoryIds).stream()
                    .collect(Collectors.toMap(SkillCategory::getId, category -> category));
        }

        for (TeachingTask task : tasks) {
            SysUser master = userMap.get(task.getMasterId());
            if (master != null) {
                task.setMasterName(master.getRealName());
            }
            SysUser apprentice = userMap.get(task.getApprenticeId());
            if (apprentice != null) {
                task.setApprenticeName(apprentice.getRealName());
            }
            HeritageProject project = projectMap.get(task.getHeritageProjectId());
            if (project != null) {
                task.setProjectName(project.getName());
            }
            SkillCategory category = skillCategoryMap.get(task.getSkillCategoryId());
            if (category != null) {
                task.setSkillCategoryName(category.getName());
            }
        }
    }
}
