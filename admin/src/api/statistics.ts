import request from '@/utils/http'

export const getOverviewStatistics = () => request.get({ url: '/api/statistics/overview' })
export const getStatisticsOverview = getOverviewStatistics
export const getMasterApprenticeStatistics = () => request.get({ url: '/api/statistics/master-apprentice' })
export const getCategoryArtworkStatistics = () => request.get({ url: '/api/statistics/category-artwork' })
export const getMonthlyGrowthStatistics = () => request.get({ url: '/api/statistics/monthly-growth' })
export const getActivityParticipationStatistics = () => request.get({ url: '/api/statistics/activity-participation' })
