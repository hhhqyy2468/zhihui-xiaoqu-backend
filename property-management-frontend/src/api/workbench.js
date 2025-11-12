import request from '@/utils/request'

/**
 * 工作台API接口
 */

// 获取工作台统计数据
export function getWorkbenchStats() {
  return request({
    url: '/api/workbench/stats',
    method: 'get'
  })
}

// 获取我的工单列表
export function getMyRepairOrderList(params) {
  return request({
    url: '/api/workbench/my-orders',
    method: 'get',
    params
  })
}

// 获取工单详情
export function getRepairOrderDetail(orderId) {
  return request({
    url: `/api/workbench/order/${orderId}`,
    method: 'get'
  })
}

// 接单
export function acceptOrder(orderId) {
  return request({
    url: `/api/workbench/order/${orderId}/accept`,
    method: 'post'
  })
}

// 开始处理
export function startProcessing(orderId) {
  return request({
    url: `/api/workbench/order/${orderId}/start`,
    method: 'post'
  })
}

// 完成工单
export function completeOrder(orderId) {
  return request({
    url: `/api/workbench/order/${orderId}/complete`,
    method: 'post'
  })
}

// 获取今日任务统计
export function getTodayTaskStats() {
  return request({
    url: '/api/workbench/today-stats',
    method: 'get'
  })
}

// 获取月度任务统计
export function getMonthlyTaskStats() {
  return request({
    url: '/api/workbench/monthly-stats',
    method: 'get'
  })
}

// 获取工单趋势数据
export function getRepairOrderTrends(params) {
  return request({
    url: '/api/workbench/order-trends',
    method: 'get',
    params
  })
}

// 获取最新维修工单
export function getRecentRepairOrders(params) {
  return request({
    url: '/api/workbench/recent-orders',
    method: 'get',
    params
  })
}

// 更新维修工单状态
export function updateRepairOrderStatus(data) {
  return request({
    url: '/api/workbench/update-order-status',
    method: 'post',
    data
  })
}

// 统一导出
const workbenchApi = {
  getStats: getWorkbenchStats,
  getMyOrderList: getMyRepairOrderList,
  getOrderDetail: getRepairOrderDetail,
  acceptOrder: acceptOrder,
  startProcessing: startProcessing,
  completeOrder: completeOrder,
  getTodayStats: getTodayTaskStats,
  getMonthlyStats: getMonthlyTaskStats,
  getOrderTrends: getRepairOrderTrends,
  getRecentOrders: getRecentRepairOrders,
  updateOrderStatus: updateRepairOrderStatus
}

export default workbenchApi