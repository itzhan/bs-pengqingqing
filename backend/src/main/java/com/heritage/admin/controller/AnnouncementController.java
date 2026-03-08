package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.AnnouncementDTO;
import com.heritage.admin.entity.Announcement;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    @AuditOperation("发布公告")
    public Result<?> createAnnouncement(@Valid @RequestBody AnnouncementDTO dto) {
        SysUser user = getCurrentUser();
        announcementService.createAnnouncement(user.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementDTO dto) {
        announcementService.updateAnnouncement(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AuditOperation("删除公告")
    public Result<?> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }

    @GetMapping
    public Result<?> listAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        PageResult<Announcement> result = announcementService.listAnnouncements(page, size, status);
        return Result.success(result);
    }

    @GetMapping("/published")
    public Result<?> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.listPublished();
        return Result.success(announcements);
    }

    @GetMapping("/{id}")
    public Result<?> getAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }
}
