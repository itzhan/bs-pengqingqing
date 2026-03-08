package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.LearningMaterialDTO;
import com.heritage.admin.entity.LearningMaterial;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.LearningMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class LearningMaterialController {

    private final LearningMaterialService materialService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/")
    @AuditOperation("上传学习材料")
    public Result<?> createMaterial(@Valid @RequestBody LearningMaterialDTO dto) {
        SysUser currentUser = getCurrentUser();
        materialService.createMaterial(currentUser.getId(), dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteMaterial(@PathVariable Long id) {
        materialService.removeById(id);
        return Result.success();
    }

    @GetMapping("/task/{taskId}")
    public Result<?> getMaterialsByTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<LearningMaterial> result = materialService.listByTask(taskId, page, size);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<?> getMyMaterials(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser currentUser = getCurrentUser();
        PageResult<LearningMaterial> result = materialService.listByUploader(currentUser.getId(), page, size);
        return Result.success(result);
    }

    @GetMapping("/")
    public Result<?> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<LearningMaterial> result = materialService.listAll(page, size);
        return Result.success(result);
    }
}
