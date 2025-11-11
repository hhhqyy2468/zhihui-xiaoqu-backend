/**
 * 简化的API模块
 * 统一管理所有API调用
 */

import request from './index'

// 认证相关
export const auth = {
  // 登录
  login: (data) => request.post('/auth/login', data),

  // 登出
  logout: () => request.post('/auth/logout'),

  // 获取用户信息
  getUserInfo: () => request.get('/auth/info')
}

// 楼宇管理
export const building = {
  // 获取楼宇列表
  getList: (params) => request.get('/building/list', params),

  // 获取楼宇详情
  getDetail: (id) => request.get(`/building/${id}`),

  // 创建楼宇
  create: (data) => request.post('/building', data),

  // 更新楼宇
  update: (id, data) => request.put(`/building/${id}`, data),

  // 删除楼宇
  delete: (id) => request.delete(`/building/${id}`)
}

// 房产管理
export const house = {
  // 获取房产列表
  getList: (params) => request.get('/house/list', params),

  // 获取房产详情
  getDetail: (id) => request.get(`/house/${id}`),

  // 创建房产
  create: (data) => request.post('/house', data),

  // 更新房产
  update: (id, data) => request.put(`/house/${id}`, data),

  // 删除房产
  delete: (id) => request.delete(`/house/${id}`)
}

// 账单管理
export const bill = {
  // 获取账单列表
  getList: (params) => request.get('/bill/list', params),

  // 获取账单详情
  getDetail: (id) => request.get(`/bill/${id}`),

  // 创建账单
  create: (data) => request.post('/bill', data),

  // 更新账单
  update: (id, data) => request.put(`/bill/${id}`, data),

  // 删除账单
  delete: (id) => request.delete(`/bill/${id}`)
}

// 维修管理
export const repair = {
  // 获取维修单列表
  getList: (params) => request.get('/repair/list', params),

  // 获取维修单详情
  getDetail: (id) => request.get(`/repair/${id}`),

  // 创建维修单
  create: (data) => request.post('/repair', data),

  // 更新维修单
  update: (id, data) => request.put(`/repair/${id}`, data),

  // 接单
  acceptOrder: (id) => request.put(`/repair/${id}/accept`),

  // 完成维修
  completeOrder: (id, data) => request.put(`/repair/${id}/complete`, data)
}

// 通知公告
export const notice = {
  // 获取公告列表
  getList: (params) => request.get('/notice/list', params),

  // 获取公告详情
  getDetail: (id) => request.get(`/notice/${id}`),

  // 创建公告
  create: (data) => request.post('/notice', data),

  // 更新公告
  update: (id, data) => request.put(`/notice/${id}`, data),

  // 删除公告
  delete: (id) => request.delete(`/notice/${id}`)
}

// 工作台数据
export const workbench = {
  // 获取统计数据
  getStatistics: () => request.get('/workbench/statistics'),

  // 获取待办事项
  getPendingItems: () => request.get('/workbench/pending'),

  // 获取最近维修单
  getRecentRepairs: () => request.get('/workbench/repairs/recent')
}

// 系统管理
export const system = {
  // 用户管理
  user: {
    getList: (params) => request.get('/system/user/list', params),
    create: (data) => request.post('/system/user', data),
    update: (id, data) => request.put(`/system/user/${id}`, data),
    delete: (id) => request.delete(`/system/user/${id}`)
  },

  // 角色管理
  role: {
    getList: (params) => request.get('/system/role/list', params),
    create: (data) => request.post('/system/role', data),
    update: (id, data) => request.put(`/system/role/${id}`, data),
    delete: (id) => request.delete(`/system/role/${id}`)
  }
}

export default {
  auth,
  building,
  house,
  bill,
  repair,
  notice,
  workbench,
  system
}