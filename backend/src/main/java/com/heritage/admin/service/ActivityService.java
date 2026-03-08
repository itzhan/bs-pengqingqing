package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.ActivityDTO;
import com.heritage.admin.entity.Activity;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    void createActivity(Long publisherId, ActivityDTO dto);
    void updateActivity(Long activityId, ActivityDTO dto);
    void deleteActivity(Long activityId);
    PageResult<Activity> listActivities(int page, int size, Integer status);
    List<Activity> listUpcoming();
}
