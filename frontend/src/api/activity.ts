import request from '@/utils/request'

export const getActivities = (params?: any) => request.get('/activities', { params })
export const getUpcomingActivities = () => request.get('/activities/upcoming')
export const getActivity = (id: number) => request.get(`/activities/${id}`)
export const participateActivity = (id: number) => request.post(`/activities/${id}/participate`)
export const cancelParticipation = (id: number) => request.delete(`/activities/${id}/participate`)
export const getParticipants = (id: number) => request.get(`/activities/${id}/participants`)
