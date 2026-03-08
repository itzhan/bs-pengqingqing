package com.heritage.admin.controller;

import com.heritage.admin.common.Result;
import com.heritage.admin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<?> getOverview() {
        Map<String, Object> stats = statisticsService.getOverview();
        return Result.success(stats);
    }

    @GetMapping("/master-apprentice")
    public Result<?> getMasterApprenticeStats() {
        List<Map<String, Object>> stats = statisticsService.getMasterApprenticeStats();
        return Result.success(stats);
    }

    @GetMapping("/category-artwork")
    public Result<?> getCategoryArtworkStats() {
        List<Map<String, Object>> stats = statisticsService.getCategoryArtworkStats();
        return Result.success(stats);
    }

    @GetMapping("/monthly-growth")
    public Result<?> getMonthlyGrowthStats() {
        List<Map<String, Object>> stats = statisticsService.getMonthlyGrowthStats();
        return Result.success(stats);
    }

    @GetMapping("/activity-participation")
    public Result<?> getActivityParticipationStats() {
        List<Map<String, Object>> stats = statisticsService.getActivityParticipationStats();
        return Result.success(stats);
    }
}
