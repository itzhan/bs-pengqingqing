package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.service.GrowthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrowthRecordServiceImpl extends ServiceImpl<GrowthRecordMapper, GrowthRecord> implements GrowthRecordService {

    @Override
    public PageResult<GrowthRecord> listByApprentice(Long apprenticeId, int page, int size, String recordType) {
        Page<GrowthRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<GrowthRecord>()
                .eq(GrowthRecord::getApprenticeId, apprenticeId);

        if (recordType != null && !recordType.isEmpty()) {
            wrapper.eq(GrowthRecord::getRecordType, recordType);
        }

        wrapper.orderByDesc(GrowthRecord::getCreatedAt);
        Page<GrowthRecord> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void createRecord(Long apprenticeId, String recordType, String title, String description, Long relatedId, String relatedType) {
        GrowthRecord record = new GrowthRecord();
        record.setApprenticeId(apprenticeId);
        record.setRecordType(recordType);
        record.setTitle(title);
        record.setDescription(description);
        record.setRelatedId(relatedId);
        record.setRelatedType(relatedType);
        save(record);
    }
}
