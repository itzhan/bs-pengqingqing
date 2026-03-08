import { ResPage } from "@/api/interface/index";
import { PORT1 } from "@/api/config/servicePort";
import http from "@/api";

/**
 * @name 审计日志模块
 */

// 获取审计日志列表
export const getAuditLogList = (params: any) => {
  return http.post<ResPage<any>>(PORT1 + `/auditLog/list`, params);
};
