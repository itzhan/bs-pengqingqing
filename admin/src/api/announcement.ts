import request from '@/utils/http'

export const getAnnouncementList = (params: any) => request.get({ url: '/api/announcements', params })
export const getAnnouncementDetail = (id: number) => request.get({ url: `/api/announcements/${id}` })
export const createAnnouncement = (data: any) => request.post({ url: '/api/announcements', params: data })
export const updateAnnouncement = (id: number, data: any) => request.put({ url: `/api/announcements/${id}`, params: data })
export const deleteAnnouncement = (id: number) => request.del({ url: `/api/announcements/${id}` })
