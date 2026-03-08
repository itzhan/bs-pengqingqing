package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.entity.AuditLog;
import com.heritage.admin.mapper.AuditLogMapper;
import com.heritage.admin.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    @Override
    public PageResult<AuditLog> listLogs(int page, int size, String operation, Long userId) {
        Page<AuditLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();

        if (operation != null && !operation.isEmpty()) {
            wrapper.eq(AuditLog::getOperation, operation);
        }
        if (userId != null) {
            wrapper.eq(AuditLog::getUserId, userId);
        }

        wrapper.orderByDesc(AuditLog::getCreatedAt);
        Page<AuditLog> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
