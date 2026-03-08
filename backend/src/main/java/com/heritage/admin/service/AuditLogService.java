package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.entity.AuditLog;

public interface AuditLogService extends IService<AuditLog> {
    PageResult<AuditLog> listLogs(int page, int size, String operation, Long userId);
}
