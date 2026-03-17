import request from '@/utils/request'

// 系统管理员统计数据
export function getAdminStats() {
  return request({
    url: '/dashboard/admin/stats',
    method: 'get'
  })
}

// 物业管理员统计数据
export function getManagerStats() {
  return request({
    url: '/dashboard/manager/stats',
    method: 'get'
  })
}
