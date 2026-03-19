package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.TeachingTaskDTO;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.entity.TeachingTask;
import com.heritage.admin.service.TeachingTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TeachingTaskController {

    private final TeachingTaskService taskService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping({"", "/"})
    @AuditOperation("发布教学任务")
    public Result<?> createTask(@Valid @RequestBody TeachingTaskDTO dto) {
        SysUser currentUser = getCurrentUser();
        taskService.createTask(currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> updateTask(@PathVariable Long id, @Valid @RequestBody TeachingTaskDTO dto) {
        SysUser currentUser = getCurrentUser();
        taskService.updateTask(id, currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser currentUser = getCurrentUser();
        taskService.updateTaskStatus(id, currentUser.getId(), status);
        return Result.success();
    }

    @GetMapping("/master")
    public Result<?> getMasterTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long apprenticeId) {
        SysUser currentUser = getCurrentUser();
        PageResult<TeachingTask> result = taskService.listByMaster(currentUser.getId(), page, size, apprenticeId);
        return Result.success(result);
    }

    @GetMapping("/apprentice")
    public Result<?> getApprenticeTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser currentUser = getCurrentUser();
        PageResult<TeachingTask> result = taskService.listByApprentice(currentUser.getId(), page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<?> getTask(@PathVariable Long id) {
        SysUser currentUser = getCurrentUser();
        TeachingTask task = taskService.getTaskDetail(id, currentUser.getId());
        return Result.success(task);
    }

    @GetMapping({"", "/"})
    public Result<?> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        PageResult<TeachingTask> result = taskService.listAll(page, size, status);
        return Result.success(result);
    }
}
