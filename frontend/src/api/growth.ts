import request from '@/utils/request'

export const getMyGrowthRecords = (params?: any) => request.get('/growth-records/my', { params })
export const getApprenticeGrowthRecords = (apprenticeId: number, params?: any) => request.get(`/growth-records/apprentice/${apprenticeId}`, { params })
export const createApprenticeGrowthRecord = (apprenticeId: number, data: any) =>
  request.post(`/growth-records/apprentice/${apprenticeId}`, data)
