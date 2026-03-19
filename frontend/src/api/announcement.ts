import request from '@/utils/request'

export const getPublishedAnnouncements = () => request.get('/announcements/published')
export const getAnnouncement = (id: number) => request.get(`/announcements/${id}`)
