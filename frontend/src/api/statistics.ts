import request from '@/utils/request'

export const getOverviewStats = () => request.get('/statistics/overview')
export const getMasterApprenticeStats = () => request.get('/statistics/master-apprentice')
export const getCategoryArtworkStats = () => request.get('/statistics/category-artwork')
export const getMonthlyGrowthStats = () => request.get('/statistics/monthly-growth')
export const getActivityParticipationStats = () => request.get('/statistics/activity-participation')
