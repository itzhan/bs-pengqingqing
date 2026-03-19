package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.ActivityDTO;
import com.heritage.admin.entity.Activity;
import com.heritage.admin.entity.ActivityParticipation;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.ActivityService;
import com.heritage.admin.service.ActivityParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityParticipationService participationService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping({"", "/"})
    @AuditOperation("发布活动")
    public Result<?> createActivity(@Valid @RequestBody ActivityDTO dto) {
        SysUser currentUser = getCurrentUser();
        activityService.createActivity(currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto) {
        activityService.updateActivity(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AuditOperation("删除活动")
    public Result<?> deleteActivity(@PathVariable Long id) {
        activityService.removeById(id);
        return Result.success();
    }

    @GetMapping({"", "/"})
    public Result<?> listActivities(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        PageResult<Activity> result = activityService.listActivities(page, size, status);
        return Result.success(result);
    }

    @GetMapping("/upcoming")
    public Result<?> getUpcomingActivities() {
        List<Activity> activities = activityService.listUpcoming();
        return Result.success(activities);
    }

    @GetMapping("/{id}")
    public Result<?> getActivity(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return Result.success(activity);
    }

    @PostMapping("/{id}/participate")
    @AuditOperation("报名活动")
    public Result<?> participate(@PathVariable Long id) {
        SysUser currentUser = getCurrentUser();
        participationService.participate(currentUser.getId(), id);
        return Result.success();
    }

    @DeleteMapping("/{id}/participate")
    public Result<?> cancelParticipation(@PathVariable Long id) {
        SysUser currentUser = getCurrentUser();
        participationService.cancelParticipation(currentUser.getId(), id);
        return Result.success();
    }

    @GetMapping("/{id}/participants")
    public Result<?> getParticipants(@PathVariable Long id) {
        List<ActivityParticipation> result = participationService.getByActivityId(id);
        return Result.success(result);
    }
}
