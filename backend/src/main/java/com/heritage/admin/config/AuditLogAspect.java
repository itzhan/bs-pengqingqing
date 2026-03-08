package com.heritage.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heritage.admin.entity.AuditLog;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    public AuditLogAspect(AuditLogMapper auditLogMapper, ObjectMapper objectMapper) {
        this.auditLogMapper = auditLogMapper;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(auditOperation)")
    public Object around(ProceedingJoinPoint point, AuditOperation auditOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        AuditLog log = new AuditLog();
        log.setOperation(auditOperation.value());
        log.setCreatedAt(LocalDateTime.now());

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setMethod(request.getMethod() + " " + request.getRequestURI());
                log.setIp(request.getRemoteAddr());
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof SysUser user) {
                log.setUserId(user.getId());
                log.setUsername(user.getUsername());
            }

            try {
                String params = objectMapper.writeValueAsString(point.getArgs());
                if (params.length() > 2000) params = params.substring(0, 2000);
                log.setParams(params);
            } catch (Exception ignored) {}

            Object result = point.proceed();
            log.setResultStatus(1);
            log.setDuration(System.currentTimeMillis() - startTime);
            auditLogMapper.insert(log);
            return result;
        } catch (Throwable e) {
            log.setResultStatus(0);
            log.setErrorMsg(e.getMessage());
            log.setDuration(System.currentTimeMillis() - startTime);
            auditLogMapper.insert(log);
            throw e;
        }
    }
}
