package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.service.GrowthRecordService;
import com.heritage.admin.vo.GrowthRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrowthRecordServiceImpl extends ServiceImpl<GrowthRecordMapper, GrowthRecord> implements GrowthRecordService {

    private final SysUserMapper sysUserMapper;

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
    public PageResult<GrowthRecordVO> listAll(int page, int size) {
        Page<GrowthRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<GrowthRecord>()
                .orderByDesc(GrowthRecord::getCreatedAt);
        Page<GrowthRecord> result = page(pageParam, wrapper);

        // 批量查询学徒姓名
        List<Long> apprenticeIds = result.getRecords().stream()
                .map(GrowthRecord::getApprenticeId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> userNameMap = java.util.Collections.emptyMap();
        if (!apprenticeIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(apprenticeIds);
            userNameMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
        }

        Map<Long, String> finalUserNameMap = userNameMap;
        List<GrowthRecordVO> voList = result.getRecords().stream().map(r -> {
            GrowthRecordVO vo = new GrowthRecordVO();
            BeanUtils.copyProperties(r, vo);
            if (r.getApprenticeId() != null) {
                vo.setApprenticeName(finalUserNameMap.getOrDefault(r.getApprenticeId(), "未知用户"));
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), result.getCurrent(), result.getSize());
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

