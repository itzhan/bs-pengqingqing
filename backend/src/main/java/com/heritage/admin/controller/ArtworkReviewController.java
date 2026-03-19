package com.heritage.admin.controller;

import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.ArtworkReviewDTO;
import com.heritage.admin.entity.ArtworkReview;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.ArtworkReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artwork-reviews")
@RequiredArgsConstructor
public class ArtworkReviewController {

    private final ArtworkReviewService reviewService;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping({"", "/"})
    @AuditOperation("点评徒弟作品")
    public Result<?> createReview(@Valid @RequestBody ArtworkReviewDTO dto) {
        SysUser currentUser = getCurrentUser();
        reviewService.createReview(currentUser.getId(), dto);
        return Result.success();
    }

    @GetMapping("/artwork/{artworkId}")
    public Result<?> getReviewsByArtwork(@PathVariable Long artworkId) {
        List<ArtworkReview> reviews = reviewService.getByArtworkId(artworkId);
        return Result.success(reviews);
    }
}
