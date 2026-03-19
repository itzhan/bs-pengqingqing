import request from '@/utils/http'

export const getArtworkList = (params: any) => request.get({ url: '/api/artworks', params })
export const getArtworkDetail = (id: number) => request.get({ url: `/api/artworks/${id}` })
export const getAllGrowthRecords = (params: any) => request.get({ url: '/api/growth-records', params })
