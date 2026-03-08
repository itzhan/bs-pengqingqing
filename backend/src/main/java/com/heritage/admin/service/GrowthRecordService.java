package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.entity.GrowthRecord;

public interface GrowthRecordService extends IService<GrowthRecord> {
    PageResult<GrowthRecord> listByApprentice(Long apprenticeId, int page, int size, String recordType);
    void createRecord(Long apprenticeId, String recordType, String title, String description, Long relatedId, String relatedType);
}
