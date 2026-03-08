import http from "@/api";

// 作品列表
export const getArtworkList = (params: any) => {
  return http.get(`/api/artworks`, params, { loading: false });
};

// 作品详情
export const getArtworkDetail = (id: number) => {
  return http.get(`/api/artworks/${id}`, {}, { loading: false });
};

// 删除作品
export const deleteArtwork = (id: number) => {
  return http.delete(`/api/artworks/${id}`);
};

// 作品点评列表
export const getArtworkReviews = (artworkId: number) => {
  return http.get(`/api/artwork-reviews/artwork/${artworkId}`, {}, { loading: false });
};

// 成长记录列表
export const getGrowthRecords = (apprenticeId: number, params: any) => {
  return http.get(`/api/growth-records/apprentice/${apprenticeId}`, params, { loading: false });
};
