package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.TeachingTaskDTO;
import com.heritage.admin.entity.TeachingTask;

public interface TeachingTaskService extends IService<TeachingTask> {
    void createTask(Long masterId, TeachingTaskDTO dto);
    void updateTask(Long taskId, Long masterId, TeachingTaskDTO dto);
    void updateTaskStatus(Long taskId, Long masterId, Integer status);
    PageResult<TeachingTask> listByMaster(Long masterId, int page, int size, Long apprenticeId);
    PageResult<TeachingTask> listByApprentice(Long apprenticeId, int page, int size);
    PageResult<TeachingTask> listAll(int page, int size, Integer status);
    TeachingTask getTaskDetail(Long taskId, Long userId);
}
