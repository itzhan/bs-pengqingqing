import http from "@/api";

// 公告列表
export const getAnnouncementList = (params: any) => {
  return http.get(`/api/announcements`, params, { loading: false });
};

// 创建公告
export const createAnnouncement = (data: any) => {
  return http.post(`/api/announcements`, data);
};

// 更新公告
export const updateAnnouncement = (id: number, data: any) => {
  return http.put(`/api/announcements/${id}`, data);
};

// 删除公告
export const deleteAnnouncement = (id: number) => {
  return http.delete(`/api/announcements/${id}`);
};
