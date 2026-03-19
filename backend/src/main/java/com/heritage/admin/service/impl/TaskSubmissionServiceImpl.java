package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.dto.SubmissionReviewDTO;
import com.heritage.admin.dto.TaskSubmissionDTO;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.entity.TaskSubmission;
import com.heritage.admin.entity.TeachingTask;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.mapper.TaskSubmissionMapper;
import com.heritage.admin.mapper.TeachingTaskMapper;
import com.heritage.admin.service.TaskSubmissionService;
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
public class TaskSubmissionServiceImpl extends ServiceImpl<TaskSubmissionMapper, TaskSubmission> implements TaskSubmissionService {

    private final GrowthRecordMapper growthRecordMapper;
    private final TeachingTaskMapper teachingTaskMapper;
    private final MasterApprenticeRelationMapper relationMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public void submitTask(Long apprenticeId, TaskSubmissionDTO dto) {
        TeachingTask task = teachingTaskMapper.selectById(dto.getTaskId());
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getApprenticeId() != null && !Objects.equals(task.getApprenticeId(), apprenticeId)) {
            throw new BusinessException("该任务未分配给你");
        }
        if (task.getApprenticeId() == null && !hasApprovedRelation(task.getMasterId(), apprenticeId)) {
            throw new BusinessException("你与该师傅不存在有效师徒关系");
        }

        // Check no duplicate submission
        TaskSubmission existing = getOne(new LambdaQueryWrapper<TaskSubmission>()
                .eq(TaskSubmission::getTaskId, dto.getTaskId())
                .eq(TaskSubmission::getApprenticeId, apprenticeId));
        if (existing != null) {
            throw new BusinessException("已提交过该任务");
        }

        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(dto.getTaskId());
        submission.setApprenticeId(apprenticeId);
        submission.setContent(dto.getContent());
        submission.setFileUrl(dto.getFileUrl());
        submission.setStatus(0); // pending
        submission.setSubmitTime(LocalDateTime.now());
        save(submission);
    }

    @Override
    public void reviewSubmission(Long submissionId, Long masterId, SubmissionReviewDTO dto) {
        TaskSubmission submission = getById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }

        TeachingTask task = teachingTaskMapper.selectById(submission.getTaskId());
        if (task == null || !task.getMasterId().equals(masterId)) {
            throw new BusinessException("无权操作");
        }

        submission.setMasterComment(dto.getMasterComment());
        submission.setScore(dto.getScore());
        submission.setStatus(1); // reviewed
        submission.setReviewTime(LocalDateTime.now());
        updateById(submission);

        // Create TASK_COMPLETE growth record
        GrowthRecord record = new GrowthRecord();
        record.setApprenticeId(submission.getApprenticeId());
        record.setRecordType("TASK_COMPLETE");
        record.setTitle("完成任务：" + task.getTitle());
        record.setDescription(dto.getMasterComment());
        record.setRelatedId(submissionId);
        record.setRelatedType("SUBMISSION");
        growthRecordMapper.insert(record);
    }

    @Override
    public List<TaskSubmission> getByTaskId(Long taskId, Long userId) {
        TeachingTask task = teachingTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        boolean isOwnerMaster = Objects.equals(task.getMasterId(), userId);
        if (!isAdmin && !isOwnerMaster) {
            throw new BusinessException("无权查看该任务提交");
        }

        List<TaskSubmission> submissions = list(new LambdaQueryWrapper<TaskSubmission>()
                .eq(TaskSubmission::getTaskId, taskId)
                .orderByDesc(TaskSubmission::getSubmitTime));
        enrichSubmissions(submissions);
        return submissions;
    }

    @Override
    public TaskSubmission getByTaskAndApprentice(Long taskId, Long apprenticeId) {
        return getOne(new LambdaQueryWrapper<TaskSubmission>()
                .eq(TaskSubmission::getTaskId, taskId)
                .eq(TaskSubmission::getApprenticeId, apprenticeId));
    }

    private boolean hasApprovedRelation(Long masterId, Long apprenticeId) {
        return relationMapper.selectCount(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, masterId)
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .eq(MasterApprenticeRelation::getStatus, 1)) > 0;
    }

    private void enrichSubmissions(List<TaskSubmission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return;
        }

        List<Long> apprenticeIds = submissions.stream()
                .map(TaskSubmission::getApprenticeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> apprenticeNameMap = Collections.emptyMap();
        if (!apprenticeIds.isEmpty()) {
            apprenticeNameMap = sysUserMapper.selectBatchIds(apprenticeIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
        }

        for (TaskSubmission submission : submissions) {
            submission.setApprenticeName(apprenticeNameMap.get(submission.getApprenticeId()));
        }
    }
}
