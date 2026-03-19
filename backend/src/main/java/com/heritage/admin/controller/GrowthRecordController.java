package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.Result;
import com.heritage.admin.dto.GrowthRecordCreateDTO;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.service.GrowthRecordService;
import com.heritage.admin.vo.GrowthRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@RestController
@RequestMapping("/api/growth-records")
@RequiredArgsConstructor
public class GrowthRecordController {

    private final GrowthRecordService recordService;
    private final MasterApprenticeRelationMapper relationMapper;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping({"", "/"})
    public Result<?> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<GrowthRecordVO> result = recordService.listAll(page, size);
        return Result.success(result);
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
        SysUser currentUser = getCurrentUser();
        if (!canViewApprenticeRecords(currentUser, apprenticeId)) {
            throw new BusinessException("无权查看该徒弟的成长记录");
        }
        PageResult<GrowthRecord> result = recordService.listByApprentice(apprenticeId, page, size, recordType);
        return Result.success(result);
    }

    @PostMapping("/apprentice/{apprenticeId}")
    public Result<?> createApprenticeRecord(
            @PathVariable Long apprenticeId,
            @Valid @RequestBody GrowthRecordCreateDTO dto) {
        SysUser currentUser = getCurrentUser();
        if (!canCreateApprenticeRecord(currentUser, apprenticeId)) {
            throw new BusinessException("无权为该徒弟新增成长记录");
        }
        recordService.createRecord(
                apprenticeId,
                dto.getRecordType(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getRelatedId(),
                dto.getRelatedType()
        );
        return Result.success();
    }

    private boolean canViewApprenticeRecords(SysUser currentUser, Long apprenticeId) {
        if (currentUser.getId().equals(apprenticeId)) {
            return true;
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return true;
        }
        if (!"MASTER".equalsIgnoreCase(currentUser.getRole())) {
            return false;
        }
        return relationMapper.selectCount(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, currentUser.getId())
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .eq(MasterApprenticeRelation::getStatus, 1)) > 0;
    }

    private boolean canCreateApprenticeRecord(SysUser currentUser, Long apprenticeId) {
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return true;
        }
        if (!"MASTER".equalsIgnoreCase(currentUser.getRole())) {
            return false;
        }
        return relationMapper.selectCount(new LambdaQueryWrapper<MasterApprenticeRelation>()
                .eq(MasterApprenticeRelation::getMasterId, currentUser.getId())
                .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                .eq(MasterApprenticeRelation::getStatus, 1)) > 0;
    }
}
