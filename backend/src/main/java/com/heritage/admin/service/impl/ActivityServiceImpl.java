package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.ActivityDTO;
import com.heritage.admin.entity.Activity;
import com.heritage.admin.mapper.ActivityMapper;
import com.heritage.admin.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public void createActivity(Long publisherId, ActivityDTO dto) {
        Activity activity = new Activity();
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setContent(dto.getContent());
        activity.setCoverUrl(dto.getCoverUrl());
        activity.setLocation(dto.getLocation());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setCurrentParticipants(0);
        activity.setHeritageProjectId(dto.getHeritageProjectId());
        activity.setStatus(0); // pending
        activity.setPublisherId(publisherId);
        save(activity);
    }

    @Override
    public void updateActivity(Long activityId, ActivityDTO dto) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setContent(dto.getContent());
        activity.setCoverUrl(dto.getCoverUrl());
        activity.setLocation(dto.getLocation());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setHeritageProjectId(dto.getHeritageProjectId());
        updateById(activity);
    }

    @Override
    public void deleteActivity(Long activityId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        removeById(activityId);
    }

    @Override
    public PageResult<Activity> listActivities(int page, int size, Integer status) {
        Page<Activity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }

        wrapper.orderByDesc(Activity::getCreatedAt);
        Page<Activity> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<Activity> listUpcoming() {
        return list(new LambdaQueryWrapper<Activity>()
                .in(Activity::getStatus, 0, 1) // 0=pending, 1=published
                .ge(Activity::getStartTime, LocalDateTime.now())
                .orderByAsc(Activity::getStartTime));
    }
}
