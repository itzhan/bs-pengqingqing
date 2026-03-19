import request from '@/utils/http'

export const getActivityList = (params: any) => request.get({ url: '/api/activities/', params })
export const getActivityDetail = (id: number) => request.get({ url: `/api/activities/${id}` })
export const createActivity = (data: any) => request.post({ url: '/api/activities/', params: data })
export const updateActivity = (id: number, data: any) => request.put({ url: `/api/activities/${id}`, params: data })
export const deleteActivity = (id: number) => request.del({ url: `/api/activities/${id}` })
