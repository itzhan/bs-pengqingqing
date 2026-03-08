package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.TeachingTaskDTO;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.TeachingTask;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.mapper.TeachingTaskMapper;
import com.heritage.admin.service.TeachingTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachingTaskServiceImpl extends ServiceImpl<TeachingTaskMapper, TeachingTask> implements TeachingTaskService {

    private final MasterApprenticeRelationMapper masterApprenticeRelationMapper;

    @Override
    public void createTask(Long masterId, TeachingTaskDTO dto) {
        TeachingTask task = new TeachingTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setMasterId(masterId);
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

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
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
    public PageResult<TeachingTask> listByMaster(Long masterId, int page, int size) {
        Page<TeachingTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TeachingTask> wrapper = new LambdaQueryWrapper<TeachingTask>()
                .eq(TeachingTask::getMasterId, masterId)
                .orderByDesc(TeachingTask::getCreatedAt);
        Page<TeachingTask> result = page(pageParam, wrapper);
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
                .orderByDesc(TeachingTask::getCreatedAt);
        Page<TeachingTask> result = page(pageParam, wrapper);
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
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
