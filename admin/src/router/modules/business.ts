import { AppRouteRecord } from '@/types/router'

export const heritageRoutes: AppRouteRecord = {
  name: 'Heritage',
  path: '/heritage',
  component: '/index/index',
  redirect: '/heritage/projects',
  meta: { title: '非遗管理', icon: 'ri:ancient-gate-line' },
  children: [
    {
      path: 'projects',
      name: 'HeritageProjects',
      component: '/heritage/projects/index',
      meta: { title: '非遗项目', icon: 'ri:book-2-line' }
    },
    {
      path: 'categories',
      name: 'SkillCategories',
      component: '/heritage/categories/index',
      meta: { title: '技艺类别', icon: 'ri:price-tag-3-line' }
    }
  ]
}

export const userRoutes: AppRouteRecord = {
  name: 'UserManage',
  path: '/user',
  component: '/index/index',
  redirect: '/user/list',
  meta: { title: '用户管理', icon: 'ri:user-line' },
  children: [
    {
      path: 'list',
      name: 'UserList',
      component: '/user/list/index',
      meta: { title: '用户列表', icon: 'ri:user-search-line' }
    }
  ]
}

export const masterRoutes: AppRouteRecord = {
  name: 'Master',
  path: '/master',
  component: '/index/index',
  redirect: '/master/profiles',
  meta: { title: '师徒管理', icon: 'ri:team-line' },
  children: [
    {
      path: 'profiles',
      name: 'MasterProfiles',
      component: '/master/profiles/index',
      meta: { title: '师傅档案', icon: 'ri:medal-line' }
    },
    {
      path: 'relations',
      name: 'MasterRelations',
      component: '/master/relations/index',
      meta: { title: '师徒关系', icon: 'ri:links-line' }
    }
  ]
}

export const teachingRoutes: AppRouteRecord = {
  name: 'Teaching',
  path: '/teaching',
  component: '/index/index',
  redirect: '/teaching/tasks',
  meta: { title: '教学管理', icon: 'ri:book-open-line' },
  children: [
    {
      path: 'tasks',
      name: 'TeachingTasks',
      component: '/teaching/tasks/index',
      meta: { title: '教学任务', icon: 'ri:task-line' }
    },
    {
      path: 'materials',
      name: 'LearningMaterials',
      component: '/teaching/materials/index',
      meta: { title: '学习材料', icon: 'ri:file-text-line' }
    }
  ]
}

export const artworkRoutes: AppRouteRecord = {
  name: 'Artwork',
  path: '/artwork',
  component: '/index/index',
  redirect: '/artwork/list',
  meta: { title: '作品管理', icon: 'ri:palette-line' },
  children: [
    {
      path: 'list',
      name: 'ArtworkList',
      component: '/artwork/list/index',
      meta: { title: '作品列表', icon: 'ri:image-line' }
    },
    {
      path: 'growth',
      name: 'GrowthRecords',
      component: '/artwork/growth/index',
      meta: { title: '成长记录', icon: 'ri:line-chart-line' }
    }
  ]
}

export const activityRoutes: AppRouteRecord = {
  name: 'Activity',
  path: '/activity',
  component: '/index/index',
  redirect: '/activity/list',
  meta: { title: '活动管理', icon: 'ri:calendar-event-line' },
  children: [
    {
      path: 'list',
      name: 'ActivityList',
      component: '/activity/list/index',
      meta: { title: '活动列表', icon: 'ri:flag-line' }
    }
  ]
}

export const announcementRoutes: AppRouteRecord = {
  name: 'Announcement',
  path: '/announcement',
  component: '/index/index',
  redirect: '/announcement/list',
  meta: { title: '公告管理', icon: 'ri:notification-line' },
  children: [
    {
      path: 'list',
      name: 'AnnouncementList',
      component: '/announcement/list/index',
      meta: { title: '公告列表', icon: 'ri:chat-quote-line' }
    }
  ]
}


