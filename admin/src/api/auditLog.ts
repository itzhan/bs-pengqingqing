import request from '@/utils/http'

export const getAuditLogList = (params: any) => request.get({ url: '/api/audit-logs/', params })
