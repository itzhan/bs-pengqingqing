import http from "@/api";

// 师傅档案列表
export const getProfileList = (params: any) => {
  return http.get(`/api/master-profiles`, params, { loading: false });
};

// 查看师傅档案
export const getProfile = (userId: number) => {
  return http.get(`/api/master-profiles/${userId}`, {}, { loading: false });
};

// 审核师傅档案
export const auditProfile = (id: number, auditStatus: number) => {
  return http.put(`/api/master-profiles/${id}/audit`, { auditStatus });
};

// 师徒关系列表
export const getRelationList = (params: any) => {
  return http.get(`/api/relations`, params, { loading: false });
};

// 审核拜师申请
export const approveRelation = (id: number, approved: boolean) => {
  return http.put(`/api/relations/${id}/approve`, { approved });
};

// 解除师徒关系
export const dissolveRelation = (id: number, reason: string) => {
  return http.put(`/api/relations/${id}/dissolve`, { reason });
};
