import request from '@/utils/request'

export const getMasterProfile = (userId: number) => request.get(`/master-profiles/${userId}`)
export const getMyMasterProfile = () => request.get('/master-profiles/my')
export const saveMasterProfile = (data: any) => request.post('/master-profiles', data)
export const getMasterProfiles = (params?: any) => request.get('/master-profiles', { params })
