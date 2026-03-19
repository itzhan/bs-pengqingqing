package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.ArtworkDTO;
import com.heritage.admin.entity.Artwork;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.ArtworkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artworks")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping({"", "/"})
    public Result<?> createArtwork(@Valid @RequestBody ArtworkDTO dto) {
        SysUser currentUser = getCurrentUser();
        artworkService.createArtwork(currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> updateArtwork(@PathVariable Long id, @Valid @RequestBody ArtworkDTO dto) {
        SysUser currentUser = getCurrentUser();
        artworkService.updateArtwork(id, currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}/submit")
    @AuditOperation("提交作品")
    public Result<?> submitArtwork(@PathVariable Long id) {
        SysUser currentUser = getCurrentUser();
        artworkService.submitArtwork(id, currentUser.getId());
        return Result.success();
    }

    @GetMapping("/my")
    public Result<?> getMyArtworks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser currentUser = getCurrentUser();
        PageResult<Artwork> result = artworkService.listByApprentice(currentUser.getId(), page, size);
        return Result.success(result);
    }

    @GetMapping("/master")
    public Result<?> getMasterApprenticesArtworks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser currentUser = getCurrentUser();
        PageResult<Artwork> result = artworkService.listByMaster(currentUser.getId(), page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<?> getArtwork(@PathVariable Long id) {
        Artwork artwork = artworkService.getById(id);
        return Result.success(artwork);
    }

    @GetMapping({"", "/"})
    public Result<?> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(artworkService.listAll(page, size, status));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteArtwork(@PathVariable Long id) {
        artworkService.removeById(id);
        return Result.success();
    }
}
