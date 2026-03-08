package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.SubmissionReviewDTO;
import com.heritage.admin.dto.TaskSubmissionDTO;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.entity.TaskSubmission;
import com.heritage.admin.service.TaskSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class TaskSubmissionController {

    private final TaskSubmissionService submissionService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/")
    @AuditOperation("提交学习成果")
    public Result<?> submit(@Valid @RequestBody TaskSubmissionDTO dto) {
        SysUser currentUser = getCurrentUser();
        submissionService.submitTask(currentUser.getId(), dto);
        return Result.success();
    }

    @GetMapping("/task/{taskId}")
    public Result<?> getSubmissionsByTask(@PathVariable Long taskId) {
        List<TaskSubmission> result = submissionService.getByTaskId(taskId);
        return Result.success(result);
    }

    @GetMapping("/my/{taskId}")
    public Result<?> getMySubmission(@PathVariable Long taskId) {
        SysUser currentUser = getCurrentUser();
        TaskSubmission submission = submissionService.getByTaskAndApprentice(taskId, currentUser.getId());
        return Result.success(submission);
    }

    @PutMapping("/{id}/review")
    @AuditOperation("点评任务提交")
    public Result<?> review(@PathVariable Long id, @Valid @RequestBody SubmissionReviewDTO dto) {
        SysUser currentUser = getCurrentUser();
        submissionService.reviewSubmission(id, currentUser.getId(), dto);
        return Result.success();
    }
}
