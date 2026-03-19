package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.dto.SubmissionReviewDTO;
import com.heritage.admin.dto.TaskSubmissionDTO;
import com.heritage.admin.entity.TaskSubmission;

import java.util.List;

public interface TaskSubmissionService extends IService<TaskSubmission> {
    void submitTask(Long apprenticeId, TaskSubmissionDTO dto);
    void reviewSubmission(Long submissionId, Long masterId, SubmissionReviewDTO dto);
    List<TaskSubmission> getByTaskId(Long taskId, Long userId);
    TaskSubmission getByTaskAndApprentice(Long taskId, Long apprenticeId);
}
