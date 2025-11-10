import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hideInMenu: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'House' }
      }
    ]
  },
  // 系统管理
  {
    path: '/system',
    name: 'System',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'Tools' }
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Document' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/system/config/index.vue'),
        meta: { title: '系统配置', icon: 'Tools' }
      },
      {
        path: 'dict',
        name: 'SystemDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Document' }
      },
      ]
  },
  // 物业管理
  {
    path: '/property',
    name: 'Property',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '物业管理', icon: 'OfficeBuilding' },
    children: [
      {
        path: 'building',
        name: 'PropertyBuilding',
        component: () => import('@/views/property/building/index.vue'),
        meta: { title: '楼栋管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'unit',
        name: 'PropertyUnit',
        component: () => import('@/views/property/unit/index.vue'),
        meta: { title: '单元管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'house',
        name: 'PropertyHouse',
        component: () => import('@/views/property/house/index.vue'),
        meta: { title: '房产管理', icon: 'OfficeBuilding' }
      },
          {
        path: 'owner',
        name: 'PropertyOwner',
        component: () => import('@/views/property/owner/index.vue'),
        meta: { title: '业主管理', icon: 'User' }
      }
    ]
  },
  // 财务管理
  {
    path: '/finance',
    name: 'Finance',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '财务管理', icon: 'Money' },
    children: [
      {
        path: 'feetype',
        name: 'FinanceFeeType',
        component: () => import('@/views/property/feetype/index.vue'),
        meta: { title: '费用类型', icon: 'Money' }
      },
      {
        path: 'bill',
        name: 'FinanceBill',
        component: () => import('@/views/property/bill/index.vue'),
        meta: { title: '账单管理', icon: 'Money' }
      },
      {
        path: 'wallet',
        name: 'FinanceWallet',
        component: () => import('@/views/property/wallet/index.vue'),
        meta: { title: '钱包管理', icon: 'Money' }
      }
    ]
  },
  // 服务管理
  {
    path: '/service',
    name: 'Service',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '服务管理', icon: 'Tools' },
    children: [
      {
        path: 'complaint',
        name: 'ServiceComplaint',
        component: () => import('@/views/property/complaint/index.vue'),
        meta: { title: '投诉管理', icon: 'Message' }
      },
      {
        path: 'repair',
        name: 'ServiceRepair',
        component: () => import('@/views/property/repair/index.vue'),
        meta: { title: '维修管理', icon: 'Tools' }
      }
    ]
  },
    // 停车管理
  {
    path: '/parking',
    name: 'Parking',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '停车管理', icon: 'Van' },
    children: [
      {
        path: 'space',
        name: 'ParkingSpace',
        component: () => import('@/views/property/parking/index.vue'),
        meta: { title: '车位管理', icon: 'Van' }
      },
      {
        path: 'rental',
        name: 'ParkingRental',
        component: () => import('@/views/property/parking/index.vue'),
        meta: { title: '租赁管理', icon: 'Van' }
      }
    ]
  },
  // 公告管理
  {
    path: '/notice',
    name: 'Notice',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '公告管理', icon: 'Bell' },
    children: [
      {
        path: 'publish',
        name: 'NoticePublish',
        component: () => import('@/views/property/notice/index.vue'),
        meta: { title: '公告发布', icon: 'Bell' }
      }
    ]
  },
  // 系统日志
  {
    path: '/log',
    name: 'Log',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '系统日志', icon: 'View' },
    children: [
      {
        path: 'operation',
        name: 'LogOperation',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', icon: 'View' }
      },
      {
        path: 'login',
        name: 'LogLogin',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '登录日志', icon: 'View' }
      }
    ]
  },
  // 业主门户（重构）
  {
    path: '/portal',
    name: 'Portal',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '业主门户', icon: 'User' },
    children: [
      {
        path: 'dashboard',
        name: 'PortalDashboard',
        component: () => import('@/views/portal/dashboard/index.vue'),
        meta: { title: '业主首页', icon: 'House' }
      },
      {
        path: 'bills',
        name: 'PortalBills',
        component: () => import('@/views/portal/bills/index.vue'),
        meta: { title: '我的账单', icon: 'Money' }
      },
      {
        path: 'wallet',
        name: 'PortalWallet',
        component: () => import('@/views/property/wallet/index.vue'),
        meta: { title: '我的钱包', icon: 'Money' }
      },
      {
        path: 'house',
        name: 'PortalHouse',
        component: () => import('@/views/property/house/index.vue'),
        meta: { title: '我的房产', icon: 'OfficeBuilding' }
      },
      {
        path: 'parking',
        name: 'PortalParking',
        component: () => import('@/views/property/parking/index.vue'),
        meta: { title: '我的车位', icon: 'Van' }
      },
      {
        path: 'complaint',
        name: 'PortalComplaint',
        component: () => import('@/views/property/complaint/index.vue'),
        meta: { title: '我的投诉', icon: 'Message' }
      },
      {
        path: 'repair',
        name: 'PortalRepair',
        component: () => import('@/views/property/repair/index.vue'),
        meta: { title: '我的报修', icon: 'Tools' }
      },
      {
        path: 'announcement',
        name: 'PortalAnnouncement',
        component: () => import('@/views/property/notice/index.vue'),
        meta: { title: '社区公告', icon: 'Bell' }
      }
    ]
  },
  // 我的工作（维修人员）
  {
    path: '/work',
    name: 'Work',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '我的工作', icon: 'Tools' },
    children: [
      {
        path: 'pending',
        name: 'WorkPending',
        component: () => import('@/views/property/repair/index.vue'),
        meta: { title: '待接单', icon: 'Tools' }
      },
      {
        path: 'processing',
        name: 'WorkProcessing',
        component: () => import('@/views/property/repair/index.vue'),
        meta: { title: '进行中', icon: 'Tools' }
      },
      {
        path: 'pending-accept',
        name: 'WorkPendingAccept',
        component: () => import('@/views/property/repair/index.vue'),
        meta: { title: '待验收', icon: 'Tools' }
      },
      {
        path: 'completed',
        name: 'WorkCompleted',
        component: () => import('@/views/property/repair/index.vue'),
        meta: { title: '已完成', icon: 'Tools' }
      }
    ]
  },
  // 社区公告（维修人员）
  {
    path: '/community-notice',
    name: 'CommunityNotice',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '社区公告', icon: 'Bell' },
    children: [
      {
        path: 'view',
        name: 'CommunityNoticeView',
        component: () => import('@/views/property/notice/index.vue'),
        meta: { title: '公告查看', icon: 'Bell' }
      }
    ]
  },
  // 个人中心
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/components/Layout/index.vue'),
    meta: { title: '个人中心', icon: 'User', hideInMenu: true },
    children: [
      {
        path: 'index',
        name: 'ProfileIndex',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User', hideInMenu: true }
      }
    ]
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard',
    meta: { hideInMenu: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 社区物业管理系统` : '社区物业管理系统'

  const token = localStorage.getItem('token')

  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router