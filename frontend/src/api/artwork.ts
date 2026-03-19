import request from '@/utils/request'

export const createArtwork = (data: any) => request.post('/artworks', data)
export const updateArtwork = (id: number, data: any) => request.put(`/artworks/${id}`, data)
export const submitArtwork = (id: number) => request.put(`/artworks/${id}/submit`)
export const getMyArtworks = (params?: any) => request.get('/artworks/my', { params })
export const getMasterArtworks = (params?: any) => request.get('/artworks/master', { params })
export const getArtworkDetail = (id: number) => request.get(`/artworks/${id}`)

export const createArtworkReview = (data: any) => request.post('/artwork-reviews', data)
export const getArtworkReviews = (artworkId: number) => request.get(`/artwork-reviews/artwork/${artworkId}`)
