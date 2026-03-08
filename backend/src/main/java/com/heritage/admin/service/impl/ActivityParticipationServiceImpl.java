package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.entity.Activity;
import com.heritage.admin.entity.ActivityParticipation;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.mapper.ActivityMapper;
import com.heritage.admin.mapper.ActivityParticipationMapper;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.service.ActivityParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityParticipationServiceImpl extends ServiceImpl<ActivityParticipationMapper, ActivityParticipation> implements ActivityParticipationService {

    private final ActivityMapper activityMapper;
    private final GrowthRecordMapper growthRecordMapper;

    @Override
    public void participate(Long userId, Long activityId) {
        // Check not already participated
        ActivityParticipation existing = getOne(new LambdaQueryWrapper<ActivityParticipation>()
                .eq(ActivityParticipation::getUserId, userId)
                .eq(ActivityParticipation::getActivityId, activityId)
                .ne(ActivityParticipation::getStatus, 2)); // not cancelled
        if (existing != null) {
            throw new BusinessException("已报名该活动");
        }

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // Check activity not full
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new BusinessException("活动已满员");
        }

        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participation.setStatus(1); // participated
        save(participation);

        // Increment currentParticipants
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);

        // Create COURSE_JOIN growth record
        GrowthRecord record = new GrowthRecord();
        record.setApprenticeId(userId);
        record.setRecordType("COURSE_JOIN");
        record.setTitle("参加活动：" + activity.getTitle());
        record.setDescription(activity.getDescription());
        record.setRelatedId(activityId);
        record.setRelatedType("ACTIVITY");
        growthRecordMapper.insert(record);
    }

    @Override
    public void cancelParticipation(Long userId, Long activityId) {
        ActivityParticipation participation = getOne(new LambdaQueryWrapper<ActivityParticipation>()
                .eq(ActivityParticipation::getUserId, userId)
                .eq(ActivityParticipation::getActivityId, activityId));
        if (participation == null) {
            throw new BusinessException("未报名该活动");
        }

        participation.setStatus(2); // cancelled
        updateById(participation);

        // Decrement currentParticipants
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityMapper.updateById(activity);
        }
    }

    @Override
    public List<ActivityParticipation> getByActivityId(Long activityId) {
        return list(new LambdaQueryWrapper<ActivityParticipation>()
                .eq(ActivityParticipation::getActivityId, activityId)
                .eq(ActivityParticipation::getStatus, 1)
                .orderByDesc(ActivityParticipation::getCreatedAt));
    }

    @Override
    public List<ActivityParticipation> getByUserId(Long userId) {
        return list(new LambdaQueryWrapper<ActivityParticipation>()
                .eq(ActivityParticipation::getUserId, userId)
                .orderByDesc(ActivityParticipation::getCreatedAt));
    }
}
