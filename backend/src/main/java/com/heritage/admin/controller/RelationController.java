package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.RelationApplyDTO;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.MasterApprenticeRelationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relations")
@RequiredArgsConstructor
public class RelationController {

    private final MasterApprenticeRelationService relationService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/apply")
    @AuditOperation("申请拜师")
    public Result<?> apply(@Valid @RequestBody RelationApplyDTO dto) {
        SysUser currentUser = getCurrentUser();
        relationService.applyRelation(currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    @AuditOperation("审核拜师申请")
    public Result<?> approve(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean approved,
            @RequestParam(required = false) Integer status) {
        SysUser currentUser = getCurrentUser();
        if (approved == null && status != null) {
            approved = status == 1;
        }
        if (approved == null) {
            throw new BusinessException("审核参数不能为空");
        }
        relationService.approveRelation(id, currentUser.getId(), approved);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @AuditOperation("撤回拜师申请")
    public Result<?> cancel(@PathVariable Long id) {
        SysUser currentUser = getCurrentUser();
        relationService.cancelRelation(id, currentUser.getId());
        return Result.success();
    }

    @PutMapping("/{id}/dissolve")
    @AuditOperation("解除师徒关系")
    public Result<?> dissolve(@PathVariable Long id, @RequestParam(required = false) String reason) {
        SysUser currentUser = getCurrentUser();
        relationService.dissolveRelation(id, currentUser.getId(), reason != null ? reason : "");
        return Result.success();
    }

    @GetMapping("/master")
    public Result<?> getMasterRelations(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long apprenticeId) {
        SysUser currentUser = getCurrentUser();
        List<MasterApprenticeRelation> result = relationService.getByMasterId(currentUser.getId(), status, apprenticeId);
        return Result.success(result);
    }

    @GetMapping("/apprentice")
    public Result<?> getApprenticeRelations(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long masterId) {
        SysUser currentUser = getCurrentUser();
        List<MasterApprenticeRelation> result = relationService.getByApprenticeId(currentUser.getId(), status, masterId);
        return Result.success(result);
    }

    @GetMapping({"", "/"})
    public Result<?> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long masterId,
            @RequestParam(required = false) Long apprenticeId) {
        PageResult<MasterApprenticeRelation> result = relationService.listRelations(page, size, status, masterId, apprenticeId);
        return Result.success(result);
    }
}
