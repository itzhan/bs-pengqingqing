package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
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
    public Result<?> approve(@PathVariable Long id, @RequestParam Boolean approved) {
        SysUser currentUser = getCurrentUser();
        relationService.approveRelation(id, currentUser.getId(), approved);
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
    public Result<?> getMasterRelations() {
        SysUser currentUser = getCurrentUser();
        List<MasterApprenticeRelation> result = relationService.getByMasterId(currentUser.getId());
        return Result.success(result);
    }

    @GetMapping("/apprentice")
    public Result<?> getApprenticeRelations() {
        SysUser currentUser = getCurrentUser();
        List<MasterApprenticeRelation> result = relationService.getByApprenticeId(currentUser.getId());
        return Result.success(result);
    }

    @GetMapping("/")
    public Result<?> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        PageResult<MasterApprenticeRelation> result = relationService.listRelations(page, size, status);
        return Result.success(result);
    }
}
