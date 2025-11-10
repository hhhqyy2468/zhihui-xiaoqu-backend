import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import { PERMISSIONS, ROLES, USER_TYPES } from '@/utils/permission'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    permissions: [],
    roles: []
  }),

  getters: {
    isLogin: (state) => !!state.token,
    username: (state) => state.userInfo.username || '',
    realName: (state) => state.userInfo.realName || '',
    userType: (state) => state.userInfo.userType || '',
    avatar: (state) => state.userInfo.avatar || '',
    hasPermission: (state) => (permission) => {
      return state.permissions.includes(permission)
    },
    hasRole: (state) => (role) => {
      return state.roles.includes(role)
    }
  },

  actions: {
    // 登录
    async login(loginForm) {
      try {
        // 开发环境下使用模拟登录
        if (import.meta.env.DEV) {
          return this.mockLogin(loginForm)
        }

        const response = await login(loginForm)
        const { token } = response.data

        this.token = token
        localStorage.setItem('token', token)

        // 获取用户信息
        await this.getUserInfo()

        return response
      } catch (error) {
        // 如果API调用失败，尝试使用模拟登录
        if (import.meta.env.DEV) {
          return this.mockLogin(loginForm)
        }
        throw error
      }
    },

    // 模拟登录（开发环境）
    mockLogin(loginForm) {
      return new Promise((resolve, reject) => {
        // 模拟API延迟
        setTimeout(() => {
          // 简单的用户验证逻辑
          const mockUsers = {
            'admin': {
              id: 1,
              username: 'admin',
              password: '123456',
              realName: '系统管理员',
              userType: USER_TYPES.ADMIN,
              avatar: '',
              phone: '13800138000',
              email: 'admin@example.com'
            },
            'manager': {
              id: 2,
              username: 'manager',
              password: '123456',
              realName: '物业经理',
              userType: USER_TYPES.MANAGER,
              avatar: '',
              phone: '13800138001',
              email: 'manager@example.com'
            },
            'owner': {
              id: 3,
              username: 'owner',
              password: '123456',
              realName: '张三',
              userType: USER_TYPES.OWNER,
              avatar: '',
              phone: '13800138002',
              email: 'owner@example.com'
            },
            'worker': {
              id: 4,
              username: 'worker',
              password: '123456',
              realName: '维修师傅',
              userType: USER_TYPES.WORKER,
              avatar: '',
              phone: '13800138003',
              email: 'worker@example.com'
            }
          }

          const user = mockUsers[loginForm.username]

          if (!user || user.password !== loginForm.password) {
            reject(new Error('用户名或密码错误'))
            return
          }

          // 生成模拟token
          const token = `mock_token_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

          // 根据用户类型设置权限和角色
          let permissions = []
          let roles = []

          switch (user.userType) {
            case USER_TYPES.ADMIN:
              roles = [ROLES.ADMIN]
              permissions = Object.values(PERMISSIONS) // 管理员拥有所有权限
              break
            case USER_TYPES.MANAGER:
              roles = [ROLES.PROPERTY_MANAGER]
              permissions = [
                // 物业管理相关权限
                PERMISSIONS.PROPERTY_BUILDING_VIEW,
                PERMISSIONS.PROPERTY_BUILDING_ADD,
                PERMISSIONS.PROPERTY_BUILDING_EDIT,
                PERMISSIONS.PROPERTY_BUILDING_DELETE,
                PERMISSIONS.PROPERTY_UNIT_VIEW,
                PERMISSIONS.PROPERTY_UNIT_ADD,
                PERMISSIONS.PROPERTY_UNIT_EDIT,
                PERMISSIONS.PROPERTY_UNIT_DELETE,
                PERMISSIONS.PROPERTY_HOUSE_VIEW,
                PERMISSIONS.PROPERTY_HOUSE_ADD,
                PERMISSIONS.PROPERTY_HOUSE_EDIT,
                PERMISSIONS.PROPERTY_HOUSE_DELETE,
                PERMISSIONS.PROPERTY_RESIDENT_VIEW,
                PERMISSIONS.PROPERTY_RESIDENT_ADD,
                PERMISSIONS.PROPERTY_RESIDENT_EDIT,
                PERMISSIONS.PROPERTY_RESIDENT_DELETE,
                PERMISSIONS.PROPERTY_FEE_TYPE_VIEW,
                PERMISSIONS.PROPERTY_FEE_TYPE_ADD,
                PERMISSIONS.PROPERTY_FEE_TYPE_EDIT,
                PERMISSIONS.PROPERTY_FEE_TYPE_DELETE,
                PERMISSIONS.PROPERTY_BILL_VIEW,
                PERMISSIONS.PROPERTY_BILL_ADD,
                PERMISSIONS.PROPERTY_BILL_EDIT,
                PERMISSIONS.PROPERTY_BILL_DELETE,
                PERMISSIONS.PROPERTY_BILL_GENERATE,
                PERMISSIONS.PROPERTY_WALLET_VIEW,
                PERMISSIONS.PROPERTY_WALLET_FREEZE,
                PERMISSIONS.PROPERTY_WALLET_RESET_PASSWORD,
                PERMISSIONS.PROPERTY_COMPLAINT_VIEW,
                PERMISSIONS.PROPERTY_COMPLAINT_ADD,
                PERMISSIONS.PROPERTY_COMPLAINT_EDIT,
                PERMISSIONS.PROPERTY_COMPLAINT_DELETE,
                PERMISSIONS.PROPERTY_COMPLAINT_ASSIGN,
                PERMISSIONS.PROPERTY_COMPLAINT_HANDLE,
                PERMISSIONS.PROPERTY_REPAIR_VIEW,
                PERMISSIONS.PROPERTY_REPAIR_ADD,
                PERMISSIONS.PROPERTY_REPAIR_EDIT,
                PERMISSIONS.PROPERTY_REPAIR_DELETE,
                PERMISSIONS.PROPERTY_REPAIR_ASSIGN,
                PERMISSIONS.PROPERTY_PARKING_VIEW,
                PERMISSIONS.PROPERTY_PARKING_ADD,
                PERMISSIONS.PROPERTY_PARKING_EDIT,
                PERMISSIONS.PROPERTY_PARKING_DELETE,
                PERMISSIONS.PROPERTY_PARKING_AUDIT,
                PERMISSIONS.PROPERTY_NOTICE_VIEW,
                PERMISSIONS.PROPERTY_NOTICE_ADD,
                PERMISSIONS.PROPERTY_NOTICE_EDIT,
                PERMISSIONS.PROPERTY_NOTICE_DELETE,
                PERMISSIONS.PROPERTY_NOTICE_PUBLISH,
                // 数据分析权限
                PERMISSIONS.ANALYTICS_DASHBOARD_VIEW,
                PERMISSIONS.ANALYTICS_REPORT_VIEW,
                PERMISSIONS.ANALYTICS_REPORT_GENERATE,
                // 消息通知权限
                PERMISSIONS.NOTIFICATION_CENTER_VIEW,
                PERMISSIONS.NOTIFICATION_CENTER_SEND,
                PERMISSIONS.NOTIFICATION_TEMPLATE_VIEW,
                PERMISSIONS.NOTIFICATION_TEMPLATE_ADD,
                PERMISSIONS.NOTIFICATION_TEMPLATE_EDIT,
                PERMISSIONS.NOTIFICATION_TEMPLATE_DELETE,
                PERMISSIONS.NOTIFICATION_SETTINGS_VIEW,
                PERMISSIONS.NOTIFICATION_SETTINGS_EDIT,
                // 业主门户查看权限（管理员可以查看业主门户）
                PERMISSIONS.PORTAL_VIEW,
                PERMISSIONS.PORTAL_DASHBOARD_VIEW,
                PERMISSIONS.PORTAL_BILL_VIEW,
                // 系统配置权限
                PERMISSIONS.SYSTEM_CONFIG_VIEW,
                PERMISSIONS.SYSTEM_CONFIG_EDIT,
                PERMISSIONS.SYSTEM_CONFIG_BACKUP,
                PERMISSIONS.SYSTEM_CONFIG_RESTORE,
                // 字典管理权限
                PERMISSIONS.SYSTEM_DICT_VIEW,
                PERMISSIONS.SYSTEM_DICT_ADD,
                PERMISSIONS.SYSTEM_DICT_EDIT,
                PERMISSIONS.SYSTEM_DICT_DELETE,
                PERMISSIONS.SYSTEM_DICT_REFRESH,
                // 系统日志权限
                PERMISSIONS.SYSTEM_LOG_VIEW,
                PERMISSIONS.SYSTEM_LOG_EXPORT,
                PERMISSIONS.SYSTEM_LOG_CLEAR,
                // 业主管理权限
                PERMISSIONS.PROPERTY_OWNER_VIEW,
                PERMISSIONS.PROPERTY_OWNER_ADD,
                PERMISSIONS.PROPERTY_OWNER_EDIT,
                PERMISSIONS.PROPERTY_OWNER_DELETE,
                PERMISSIONS.PROPERTY_OWNER_IMPORT,
                PERMISSIONS.PROPERTY_OWNER_EXPORT
              ]
              break
            case USER_TYPES.OWNER:
              roles = [ROLES.OWNER]
              permissions = [
                // 业主门户权限
                PERMISSIONS.PORTAL_VIEW,
                PERMISSIONS.PORTAL_DASHBOARD_VIEW,
                PERMISSIONS.PORTAL_BILL_VIEW,
                PERMISSIONS.PORTAL_BILL_PAY,
                PERMISSIONS.PORTAL_PROFILE_VIEW,
                PERMISSIONS.PORTAL_PROFILE_EDIT,
                PERMISSIONS.PORTAL_SERVICE_APPLY,
                // 财务相关权限
                PERMISSIONS.PROPERTY_BILL_VIEW,
                PERMISSIONS.PROPERTY_BILL_PAY,
                PERMISSIONS.PROPERTY_WALLET_VIEW,
                PERMISSIONS.PROPERTY_WALLET_RECHARGE,
                // 服务相关权限
                PERMISSIONS.PROPERTY_COMPLAINT_ADD,
                PERMISSIONS.PROPERTY_COMPLAINT_RATE,
                PERMISSIONS.PROPERTY_REPAIR_ADD,
                PERMISSIONS.PROPERTY_REPAIR_ACCEPT,
                // 信息查看权限
                PERMISSIONS.PROPERTY_NOTICE_VIEW
              ]
              break
            case USER_TYPES.WORKER:
              roles = [ROLES.WORKER]
              permissions = [
                PERMISSIONS.PROPERTY_REPAIR_VIEW,
                PERMISSIONS.PROPERTY_REPAIR_HANDLE,
                PERMISSIONS.PROPERTY_NOTICE_VIEW
              ]
              break
          }

          // 设置用户信息
          this.token = token
          this.userInfo = { ...user }
          this.permissions = permissions
          this.roles = roles

          // 保存到localStorage
          localStorage.setItem('token', token)
          localStorage.setItem('userInfo', JSON.stringify(user))

          resolve({
            data: { token },
            msg: '登录成功'
          })
        }, 500)
      })
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        // 开发环境下如果token是mock token，返回缓存的用户信息
        if (import.meta.env.DEV && this.token.startsWith('mock_token_')) {
          const cachedUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
          return {
            data: {
              user: cachedUser,
              permissions: this.permissions,
              roles: this.roles
            }
          }
        }

        const response = await getUserInfo()
        const { user, permissions, roles } = response.data

        this.userInfo = user
        this.permissions = permissions || []
        this.roles = roles || []

        localStorage.setItem('userInfo', JSON.stringify(user))

        return response
      } catch (error) {
        this.logout()
        throw error
      }
    },

    // 登出
    async logout() {
      try {
        if (!this.token.startsWith('mock_token_')) {
          await logout()
        }
      } catch (error) {
        console.error('登出接口调用失败:', error)
      } finally {
        this.token = ''
        this.userInfo = {}
        this.permissions = []
        this.roles = []

        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    },

    // 重置状态
    resetState() {
      this.token = ''
      this.userInfo = {}
      this.permissions = []
      this.roles = []

      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },

    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    // 初始化用户状态（页面刷新时调用）
    initializeAuth() {
      const token = localStorage.getItem('token')
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

      if (token && userInfo.username) {
        this.token = token
        this.userInfo = userInfo

        // 根据用户类型重新设置权限和角色
        let permissions = []
        let roles = []

        switch (userInfo.userType) {
          case USER_TYPES.ADMIN:
            roles = [ROLES.ADMIN]
            permissions = Object.values(PERMISSIONS) // 管理员拥有所有权限
            break
          case USER_TYPES.MANAGER:
            roles = [ROLES.PROPERTY_MANAGER]
            permissions = [
              // 物业管理相关权限
              PERMISSIONS.PROPERTY_BUILDING_VIEW,
              PERMISSIONS.PROPERTY_BUILDING_ADD,
              PERMISSIONS.PROPERTY_BUILDING_EDIT,
              PERMISSIONS.PROPERTY_BUILDING_DELETE,
              PERMISSIONS.PROPERTY_UNIT_VIEW,
              PERMISSIONS.PROPERTY_UNIT_ADD,
              PERMISSIONS.PROPERTY_UNIT_EDIT,
              PERMISSIONS.PROPERTY_UNIT_DELETE,
              PERMISSIONS.PROPERTY_HOUSE_VIEW,
              PERMISSIONS.PROPERTY_HOUSE_ADD,
              PERMISSIONS.PROPERTY_HOUSE_EDIT,
              PERMISSIONS.PROPERTY_HOUSE_DELETE,
              PERMISSIONS.PROPERTY_RESIDENT_VIEW,
              PERMISSIONS.PROPERTY_RESIDENT_ADD,
              PERMISSIONS.PROPERTY_RESIDENT_EDIT,
              PERMISSIONS.PROPERTY_RESIDENT_DELETE,
              PERMISSIONS.PROPERTY_FEE_TYPE_VIEW,
              PERMISSIONS.PROPERTY_FEE_TYPE_ADD,
              PERMISSIONS.PROPERTY_FEE_TYPE_EDIT,
              PERMISSIONS.PROPERTY_FEE_TYPE_DELETE,
              PERMISSIONS.PROPERTY_BILL_VIEW,
              PERMISSIONS.PROPERTY_BILL_ADD,
              PERMISSIONS.PROPERTY_BILL_EDIT,
              PERMISSIONS.PROPERTY_BILL_DELETE,
              PERMISSIONS.PROPERTY_BILL_GENERATE,
              PERMISSIONS.PROPERTY_BILL_PAY,
              PERMISSIONS.PROPERTY_WALLET_VIEW,
              PERMISSIONS.PROPERTY_WALLET_RECHARGE,
              PERMISSIONS.PROPERTY_WALLET_FREEZE,
              PERMISSIONS.PROPERTY_WALLET_RESET_PASSWORD,
              PERMISSIONS.PROPERTY_COMPLAINT_VIEW,
              PERMISSIONS.PROPERTY_COMPLAINT_ADD,
              PERMISSIONS.PROPERTY_COMPLAINT_EDIT,
              PERMISSIONS.PROPERTY_COMPLAINT_DELETE,
              PERMISSIONS.PROPERTY_COMPLAINT_ASSIGN,
              PERMISSIONS.PROPERTY_COMPLAINT_HANDLE,
              PERMISSIONS.PROPERTY_COMPLAINT_RATE,
              PERMISSIONS.PROPERTY_REPAIR_VIEW,
              PERMISSIONS.PROPERTY_REPAIR_ADD,
              PERMISSIONS.PROPERTY_REPAIR_EDIT,
              PERMISSIONS.PROPERTY_REPAIR_DELETE,
              PERMISSIONS.PROPERTY_REPAIR_ASSIGN,
              PERMISSIONS.PROPERTY_REPAIR_HANDLE,
              PERMISSIONS.PROPERTY_REPAIR_ACCEPT,
              PERMISSIONS.PROPERTY_PARKING_VIEW,
              PERMISSIONS.PROPERTY_PARKING_ADD,
              PERMISSIONS.PROPERTY_PARKING_EDIT,
              PERMISSIONS.PROPERTY_PARKING_DELETE,
              PERMISSIONS.PROPERTY_PARKING_RENT,
              PERMISSIONS.PROPERTY_PARKING_AUDIT,
              PERMISSIONS.PROPERTY_PARKING_RENEW,
              PERMISSIONS.PROPERTY_PARKING_RETURN,
              PERMISSIONS.PROPERTY_NOTICE_VIEW,
              PERMISSIONS.PROPERTY_NOTICE_ADD,
              PERMISSIONS.PROPERTY_NOTICE_EDIT,
              PERMISSIONS.PROPERTY_NOTICE_DELETE,
              PERMISSIONS.PROPERTY_NOTICE_PUBLISH,
              PERMISSIONS.PROPERTY_NOTICE_WITHDRAW,
              // 数据分析权限
              PERMISSIONS.ANALYTICS_DASHBOARD_VIEW,
              PERMISSIONS.ANALYTICS_DASHBOARD_EXPORT,
              PERMISSIONS.ANALYTICS_REPORT_VIEW,
              PERMISSIONS.ANALYTICS_REPORT_GENERATE,
              PERMISSIONS.ANALYTICS_REPORT_DOWNLOAD,
              PERMISSIONS.ANALYTICS_REPORT_SHARE,
              PERMISSIONS.ANALYTICS_REPORT_DELETE,
              PERMISSIONS.ANALYTICS_REPORT_TEMPLATE,
              PERMISSIONS.ANALYTICS_REPORT_SCHEDULE,
              // 消息通知权限
              PERMISSIONS.NOTIFICATION_CENTER_VIEW,
              PERMISSIONS.NOTIFICATION_CENTER_SEND,
              PERMISSIONS.NOTIFICATION_CENTER_DELETE,
              PERMISSIONS.NOTIFICATION_CENTER_BATCH,
              PERMISSIONS.NOTIFICATION_TEMPLATE_VIEW,
              PERMISSIONS.NOTIFICATION_TEMPLATE_ADD,
              PERMISSIONS.NOTIFICATION_TEMPLATE_EDIT,
              PERMISSIONS.NOTIFICATION_TEMPLATE_DELETE,
              PERMISSIONS.NOTIFICATION_SETTINGS_VIEW,
              PERMISSIONS.NOTIFICATION_SETTINGS_EDIT,
              // 系统配置权限
              PERMISSIONS.SYSTEM_CONFIG_VIEW,
              PERMISSIONS.SYSTEM_CONFIG_EDIT,
              PERMISSIONS.SYSTEM_CONFIG_BACKUP,
              PERMISSIONS.SYSTEM_CONFIG_RESTORE
            ]
            break
          case USER_TYPES.OWNER:
            roles = [ROLES.OWNER]
            permissions = [
              // 业主门户权限
              PERMISSIONS.PORTAL_VIEW,
              PERMISSIONS.PORTAL_DASHBOARD_VIEW,
              PERMISSIONS.PORTAL_BILL_VIEW,
              PERMISSIONS.PORTAL_BILL_PAY,
              PERMISSIONS.PORTAL_SERVICE_APPLY,
              PERMISSIONS.PORTAL_PROFILE_VIEW,
              PERMISSIONS.PORTAL_PROFILE_EDIT,
              // 基础查看权限
              PERMISSIONS.PROPERTY_NOTICE_VIEW,
              PERMISSIONS.ANALYTICS_DASHBOARD_VIEW
            ]
            break
          case USER_TYPES.WORKER:
            roles = [ROLES.WORKER]
            permissions = [
              // 维修相关权限
              PERMISSIONS.PROPERTY_REPAIR_VIEW,
              PERMISSIONS.PROPERTY_REPAIR_HANDLE,
              PERMISSIONS.PROPERTY_REPAIR_ACCEPT,
              // 基础权限
              PERMISSIONS.NOTIFICATION_CENTER_VIEW
            ]
            break
        }

        this.permissions = permissions
        this.roles = roles
      }
    }
  }
})