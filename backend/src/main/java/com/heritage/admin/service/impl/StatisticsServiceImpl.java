package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heritage.admin.entity.*;
import com.heritage.admin.mapper.*;
import com.heritage.admin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SysUserMapper sysUserMapper;
    private final HeritageProjectMapper heritageProjectMapper;
    private final ArtworkMapper artworkMapper;
    private final ActivityMapper activityMapper;
    private final MasterApprenticeRelationMapper masterApprenticeRelationMapper;
    private final SkillCategoryMapper skillCategoryMapper;
    private final GrowthRecordMapper growthRecordMapper;
    private final ActivityParticipationMapper activityParticipationMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", sysUserMapper.selectCount(null));
        result.put("totalMasters", sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "MASTER")));
        result.put("totalApprentices", sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "APPRENTICE")));
        result.put("totalProjects", heritageProjectMapper.selectCount(null));
        result.put("totalArtworks", artworkMapper.selectCount(null));
        result.put("totalActivities", activityMapper.selectCount(null));
        result.put("totalRelations", masterApprenticeRelationMapper.selectCount(
                new LambdaQueryWrapper<MasterApprenticeRelation>().eq(MasterApprenticeRelation::getStatus, 1)));
        return result;
    }

    @Override
    public List<Map<String, Object>> getMasterApprenticeStats() {
        List<MasterApprenticeRelation> relations = masterApprenticeRelationMapper.selectList(
                new LambdaQueryWrapper<MasterApprenticeRelation>()
                        .eq(MasterApprenticeRelation::getStatus, 1));

        Map<Long, Long> masterCountMap = relations.stream()
                .collect(Collectors.groupingBy(
                        MasterApprenticeRelation::getMasterId,
                        Collectors.counting()));

        return masterCountMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    SysUser master = sysUserMapper.selectById(entry.getKey());
                    stat.put("masterName", master != null ? master.getRealName() : "未知");
                    stat.put("apprenticeCount", entry.getValue());
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCategoryArtworkStats() {
        List<Artwork> artworks = artworkMapper.selectList(null);
        Map<Long, Long> categoryCountMap = artworks.stream()
                .filter(a -> a.getSkillCategoryId() != null)
                .collect(Collectors.groupingBy(
                        Artwork::getSkillCategoryId,
                        Collectors.counting()));

        return categoryCountMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    SkillCategory category = skillCategoryMapper.selectById(entry.getKey());
                    stat.put("categoryName", category != null ? category.getName() : "未知");
                    stat.put("artworkCount", entry.getValue());
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getMonthlyGrowthStats() {
        LocalDateTime twelveMonthsAgo = LocalDateTime.now().minusMonths(12);
        List<GrowthRecord> records = growthRecordMapper.selectList(
                new LambdaQueryWrapper<GrowthRecord>()
                        .ge(GrowthRecord::getCreatedAt, twelveMonthsAgo));

        Map<String, Long> monthlyCount = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));

        return monthlyCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("month", entry.getKey());
                    stat.put("count", entry.getValue());
                    return stat;
                })
                .sorted((a, b) -> ((String) a.get("month")).compareTo((String) b.get("month")))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getActivityParticipationStats() {
        List<ActivityParticipation> participations = activityParticipationMapper.selectList(
                new LambdaQueryWrapper<ActivityParticipation>()
                        .eq(ActivityParticipation::getStatus, 1));

        Map<Long, Long> activityCountMap = participations.stream()
                .collect(Collectors.groupingBy(
                        ActivityParticipation::getActivityId,
                        Collectors.counting()));

        return activityCountMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    Activity activity = activityMapper.selectById(entry.getKey());
                    stat.put("activityTitle", activity != null ? activity.getTitle() : "未知");
                    stat.put("participantCount", entry.getValue());
                    return stat;
                })
                .collect(Collectors.toList());
    }
}
