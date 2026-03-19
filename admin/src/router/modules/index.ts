import { AppRouteRecord } from '@/types/router'
import { dashboardRoutes } from './dashboard'
import {
  heritageRoutes,
  userRoutes,
  masterRoutes,
  teachingRoutes,
  artworkRoutes,
  activityRoutes,
  announcementRoutes
} from './business'

/**
 * 导出所有模块化路由
 */
export const routeModules: AppRouteRecord[] = [
  dashboardRoutes,
  heritageRoutes,
  userRoutes,
  masterRoutes,
  teachingRoutes,
  artworkRoutes,
  activityRoutes,
  announcementRoutes
]
