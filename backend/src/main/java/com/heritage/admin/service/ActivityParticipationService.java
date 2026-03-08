package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.entity.ActivityParticipation;

import java.util.List;

public interface ActivityParticipationService extends IService<ActivityParticipation> {
    void participate(Long userId, Long activityId);
    void cancelParticipation(Long userId, Long activityId);
    List<ActivityParticipation> getByActivityId(Long activityId);
    List<ActivityParticipation> getByUserId(Long userId);
}
