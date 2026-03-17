import request from '@/utils/request'

// ===================== 操作日志 =====================

/**
 * 分页查询操作日志
 */
export function listOperLog(params) {
  return request({
    url: '/system/log/operation/list',
    method: 'get',
    params
  })
}

/**
 * 获取操作日志详情
 */
export function getOperLog(operId) {
  return request({
    url: `/system/log/operation/${operId}`,
    method: 'get'
  })
}

/**
 * 删除操作日志
 */
export function delOperLog(operIds) {
  return request({
    url: `/system/log/operation/${operIds}`,
    method: 'delete'
  })
}

/**
 * 清理操作日志（按时间）
 */
export function cleanOperLog(beforeTime) {
  return request({
    url: '/system/log/operation/clean',
    method: 'delete',
    params: { beforeTime }
  })
}

/**
 * 获取操作日志统计
 */
export function getOperLogStatistics() {
  return request({
    url: '/system/log/operation/statistics',
    method: 'get'
  })
}

// ===================== 登录日志 =====================

/**
 * 分页查询登录日志
 */
export function listLoginLog(params) {
  return request({
    url: '/system/log/login/list',
    method: 'get',
    params
  })
}

/**
 * 获取登录日志详情
 */
export function getLoginLog(id) {
  return request({
    url: `/system/log/login/${id}`,
    method: 'get'
  })
}

/**
 * 删除登录日志
 */
export function delLoginLog(ids) {
  return request({
    url: `/system/log/login/${ids}`,
    method: 'delete'
  })
}

/**
 * 清理登录日志（按时间）
 */
export function cleanLoginLog(beforeTime) {
  return request({
    url: '/system/log/login/clean',
    method: 'delete',
    params: { beforeTime }
  })
}

/**
 * 获取登录日志统计
 */
export function getLoginLogStatistics() {
  return request({
    url: '/system/log/login/statistics',
    method: 'get'
  })
}

/**
 * 获取今日登录统计
 */
export function getTodayLoginStatistics() {
  return request({
    url: '/system/log/login/today',
    method: 'get'
  })
}
