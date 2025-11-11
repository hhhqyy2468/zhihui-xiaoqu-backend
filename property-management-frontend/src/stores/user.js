/**
 * 简化的用户状态管理
 * 移除复杂的权限系统，专注于基础功能
 */

import { defineStore } from 'pinia'
import api from '../api/modules'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]'),
    roles: JSON.parse(localStorage.getItem('roles') || '[]')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    realName: (state) => state.userInfo.realName || '用户',
    userType: (state) => state.userInfo.userType || 1,
    username: (state) => state.userInfo.username || '',
    userId: (state) => state.userInfo.userId || state.userInfo.id || null,
    permissions: (state) => state.permissions,
    roles: (state) => state.roles,
    hasPermission: (state) => (permission) => {
      if (!permission) return true
      if (typeof permission === 'string') {
        return state.permissions.includes(permission)
      }
      if (Array.isArray(permission)) {
        return permission.some(p => state.permissions.includes(p))
      }
      return false
    }
  },

  actions: {
    // 简化的登录
    async login(credentials) {
      try {
        // 构造后端期望的登录数据格式
        const loginData = {
          '@class': 'com.hyu.common.domain.LoginBody',
          username: credentials.username,
          password: credentials.password
        }

        const response = await api.auth.login(loginData)
        const { token } = response.data

        this.token = token
        localStorage.setItem('token', token)

        // 获取用户信息
        await this.fetchUserInfo()

        return { success: true }
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },

    // 模拟登录（用于测试）
    mockLogin(username, password = '123456') {
      console.log('尝试登录:', username, password)

      const mockUsers = {
        admin: {
          id: 1,
          username: 'admin',
          realName: '系统管理员',
          userType: 1, // 管理员
          phone: '13800138000'
        },
        manager: {
          id: 2,
          username: 'manager',
          realName: '物业经理',
          userType: 2, // 物业管理员
          phone: '13800138001'
        },
        owner: {
          id: 3,
          username: 'owner',
          realName: '张三',
          userType: 3, // 业主
          phone: '13800138002'
        },
        worker: {
          id: 4,
          username: 'worker',
          realName: '维修师傅',
          userType: 4, // 维修人员
          phone: '13800138003'
        },
        // 支持真实数据库用户 worker_a
        worker_a: {
          id: 5,
          username: 'worker_a',
          realName: '维修工A',
          userType: 4, // 维修人员
          phone: '13800138005'
        }
      }

      const user = mockUsers[username]
      console.log('找到用户:', user)

      if (!user) {
        console.error('用户不存在:', username)
        throw new Error('用户不存在，请使用: admin, manager, owner, worker, worker_a')
      }

      // 简化密码验证，只要密码不是空就通过
      if (!password || password.trim() === '') {
        throw new Error('密码不能为空')
      }

      const token = `mock_token_${Date.now()}`

      this.token = token
      this.userInfo = user

      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(user))

      console.log('登录成功:', user.realName)
      return { success: true }
    },

    // 获取用户信息和权限
    async fetchUserInfo() {
      try {
        // 先获取基本信息
        const userResponse = await api.auth.getUserInfo()
        this.userInfo = userResponse.data
        localStorage.setItem('userInfo', JSON.stringify(userResponse.data))

        // 再获取权限信息
        await this.fetchPermissions()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取失败，尝试使用缓存的信息
        const cached = localStorage.getItem('userInfo')
        if (cached) {
          this.userInfo = JSON.parse(cached)
        }
        const cachedPermissions = localStorage.getItem('permissions')
        if (cachedPermissions) {
          this.permissions = JSON.parse(cachedPermissions)
        }
      }
    },

    // 获取权限信息
    async fetchPermissions() {
      try {
        // 调用权限测试接口获取权限
        const response = await fetch('/test/permission/current', {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })

        if (response.ok) {
          const data = await response.json()
          if (data.success && data.data) {
            this.permissions = data.data.permissions || []
            this.roles = data.data.roles || []
            localStorage.setItem('permissions', JSON.stringify(this.permissions))
            localStorage.setItem('roles', JSON.stringify(this.roles))
            console.log('获取权限成功:', this.permissions)
          }
        }
      } catch (error) {
        console.error('获取权限信息失败:', error)
        // 如果获取失败，尝试使用缓存
        const cachedPermissions = localStorage.getItem('permissions')
        if (cachedPermissions) {
          this.permissions = JSON.parse(cachedPermissions)
        }
      }
    },

    // 登出
    async logout() {
      try {
        await api.auth.logout()
      } catch (error) {
        console.error('登出接口调用失败:', error)
      } finally {
        this.token = ''
        this.userInfo = {}
        this.permissions = []
        this.roles = []
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('permissions')
        localStorage.removeItem('roles')
      }
    },

    // 初始化用户状态（页面刷新时）
    initUserState() {
      const token = localStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo')
      const permissions = localStorage.getItem('permissions')
      const roles = localStorage.getItem('roles')

      if (token && userInfo) {
        this.token = token
        this.userInfo = JSON.parse(userInfo)
        this.permissions = permissions ? JSON.parse(permissions) : []
        this.roles = roles ? JSON.parse(roles) : []
      }
    },

    // 检查权限（简化版本）
    hasMenuAccess(menuPath) {
      const { userType } = this

      // 菜单权限映射
      const menuPermissions = {
        // 系统管理 - 仅管理员
        '/system': [1],
        // 物业管理 - 管理员和物业管理员
        '/property': [1, 2],
        '/property/building': [1, 2],
        '/property/unit': [1, 2],
        '/property/house': [1, 2],
        '/property/owner': [1, 2],
        // 财务管理 - 管理员和物业管理员
        '/finance': [1, 2],
        '/finance/feetype': [1, 2],
        '/finance/bill': [1, 2],
        '/finance/wallet': [1, 2],
        // 服务管理 - 管理员和物业管理员
        '/service': [1, 2],
        '/service/complaint': [1, 2],
        '/service/repair': [1, 2],
        // 停车管理 - 管理员和物业管理员
        '/parking': [1, 2],
        '/parking/space': [1, 2],
        '/parking/rental': [1, 2],
        // 公告管理 - 管理员和物业管理员
        '/notice': [1, 2],
        '/notice/publish': [1, 2],
        // 系统日志 - 仅管理员
        '/log': [1],
        // 业主门户 - 仅业主
        '/portal': [3],
        // 我的工作 - 仅维修人员
        '/work': [4],
        // 社区公告 - 维修人员可见
        '/community-notice': [4],
        // 工作台 - 所有角色
        '/dashboard': [1, 2, 3, 4]
      }

      const allowedTypes = menuPermissions[menuPath]
      return allowedTypes ? allowedTypes.includes(userType) : false
    }
  }
})