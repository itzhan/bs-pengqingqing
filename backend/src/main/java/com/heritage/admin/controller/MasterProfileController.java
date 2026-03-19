package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.MasterProfileDTO;
import com.heritage.admin.entity.MasterProfile;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.MasterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-profiles")
@RequiredArgsConstructor
public class MasterProfileController {

    private final MasterProfileService profileService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/my")
    public Result<?> getMyProfile() {
        SysUser currentUser = getCurrentUser();
        MasterProfile profile = profileService.getByUserId(currentUser.getId());
        return Result.success(profile);
    }

    @GetMapping("/{userId}")
    public Result<?> getProfileByUserId(@PathVariable Long userId) {
        MasterProfile profile = profileService.getByUserId(userId);
        return Result.success(profile);
    }

    @PostMapping({"", "/"})
    public Result<?> createOrUpdateProfile(@Valid @RequestBody MasterProfileDTO dto) {
        SysUser currentUser = getCurrentUser();
        profileService.saveOrUpdateProfile(currentUser.getId(), dto);
        return Result.success();
    }

    @GetMapping({"", "/"})
    public Result<?> listProfiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer auditStatus) {
        PageResult<MasterProfile> result = profileService.listProfiles(page, size, auditStatus);
        return Result.success(result);
    }

    @PutMapping("/{id}/audit")
    @AuditOperation("审核师傅档案")
    public Result<?> auditProfile(@PathVariable Long id, @RequestParam Integer auditStatus) {
        profileService.auditProfile(id, auditStatus);
        return Result.success();
    }
}
