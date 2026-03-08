package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.GrowthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/growth-records")
@RequiredArgsConstructor
public class GrowthRecordController {

    private final GrowthRecordService recordService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/my")
    public Result<?> getMyRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String recordType) {
        SysUser currentUser = getCurrentUser();
        PageResult<GrowthRecord> result = recordService.listByApprentice(currentUser.getId(), page, size, recordType);
        return Result.success(result);
    }

    @GetMapping("/apprentice/{apprenticeId}")
    public Result<?> getApprenticeRecords(
            @PathVariable Long apprenticeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String recordType) {
        PageResult<GrowthRecord> result = recordService.listByApprentice(apprenticeId, page, size, recordType);
        return Result.success(result);
    }
}
