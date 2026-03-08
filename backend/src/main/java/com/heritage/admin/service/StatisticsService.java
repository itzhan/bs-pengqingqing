package com.heritage.admin.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getMasterApprenticeStats();
    List<Map<String, Object>> getCategoryArtworkStats();
    List<Map<String, Object>> getMonthlyGrowthStats();
    List<Map<String, Object>> getActivityParticipationStats();
}
