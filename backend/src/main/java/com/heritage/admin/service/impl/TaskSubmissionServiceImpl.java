package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.dto.SubmissionReviewDTO;
import com.heritage.admin.dto.TaskSubmissionDTO;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.TaskSubmission;
import com.heritage.admin.entity.TeachingTask;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.TaskSubmissionMapper;
import com.heritage.admin.mapper.TeachingTaskMapper;
import com.heritage.admin.service.TaskSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskSubmissionServiceImpl extends ServiceImpl<TaskSubmissionMapper, TaskSubmission> implements TaskSubmissionService {

    private final GrowthRecordMapper growthRecordMapper;
    private final TeachingTaskMapper teachingTaskMapper;

    @Override
    public void submitTask(Long apprenticeId, TaskSubmissionDTO dto) {
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
    public List<TaskSubmission> getByTaskId(Long taskId) {
        return list(new LambdaQueryWrapper<TaskSubmission>()
                .eq(TaskSubmission::getTaskId, taskId)
                .orderByDesc(TaskSubmission::getSubmitTime));
    }

    @Override
    public TaskSubmission getByTaskAndApprentice(Long taskId, Long apprenticeId) {
        return getOne(new LambdaQueryWrapper<TaskSubmission>()
                .eq(TaskSubmission::getTaskId, taskId)
                .eq(TaskSubmission::getApprenticeId, apprenticeId));
    }
}
