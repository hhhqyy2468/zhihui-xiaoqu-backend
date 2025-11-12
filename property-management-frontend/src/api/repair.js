import request from '@/utils/request'

/**
 * 维修工单管理API接口
 */

// 获取维修工单列表
export function getRepairOrderList(params) {
  return request({
    url: '/api/property/repair/list',
    method: 'get',
    params
  })
}

// 获取维修工单详情
export function getRepairOrderDetail(orderId) {
  return request({
    url: `/api/property/repair/${orderId}`,
    method: 'get'
  })
}

// 新增维修工单
export function addRepairOrder(data) {
  return request({
    url: '/api/property/repair',
    method: 'post',
    data
  })
}

// 更新维修工单
export function updateRepairOrder(data) {
  return request({
    url: '/api/property/repair',
    method: 'put',
    data
  })
}

// 删除维修工单
export function deleteRepairOrder(orderId) {
  return request({
    url: `/api/property/repair/${orderId}`,
    method: 'delete'
  })
}

// 批量删除维修工单
export function batchDeleteRepairOrders(orderIds) {
  return request({
    url: '/api/property/repair/batch',
    method: 'delete',
    data: { orderIds }
  })
}

// 分配维修工单
export function assignRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/assign`,
    method: 'put',
    data
  })
}

// 接单
export function acceptRepairOrder(orderId) {
  return request({
    url: `/api/property/repair/${orderId}/accept`,
    method: 'put'
  })
}

// 开始处理
export function startRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/start`,
    method: 'put',
    data
  })
}

// 处理维修工单
export function processRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/process`,
    method: 'put',
    data
  })
}

// 完成工单
export function completeRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/complete`,
    method: 'put',
    data
  })
}

// 取消工单
export function cancelRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/cancel`,
    method: 'put',
    data
  })
}

// 重新分配
export function reassignRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/reassign`,
    method: 'put',
    data
  })
}

// 获取维修工单统计
export function getRepairOrderStats(params) {
  return request({
    url: '/api/property/repair/stats',
    method: 'get',
    params
  })
}

// 获取我的维修工单
export function getMyRepairOrders(params) {
  return request({
    url: '/api/property/repair/my-orders',
    method: 'get',
    params
  })
}

// 获取维修人员列表
export function getRepairStaffList(params) {
  return request({
    url: '/api/property/repair/staff',
    method: 'get',
    params
  })
}

// 添加维修记录
export function addRepairRecord(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/record`,
    method: 'post',
    data
  })
}

// 获取维修记录
export function getRepairRecords(orderId) {
  return request({
    url: `/api/property/repair/${orderId}/records`,
    method: 'get'
  })
}

// 上传维修图片
export function uploadRepairImages(orderId, files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  formData.append('businessType', 'repair')
  formData.append('businessId', orderId)

  return request({
    url: `/api/property/repair/${orderId}/images`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取维修工单趋势
export function getRepairOrderTrends(params) {
  return request({
    url: '/api/property/repair/trends',
    method: 'get',
    params
  })
}

// 导出维修工单
export function exportRepairOrders(params) {
  return request({
    url: '/api/property/repair/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取维修类型
export function getRepairTypes() {
  return request({
    url: '/api/property/repair/types',
    method: 'get'
  })
}

// 获取紧急程度
export function getUrgencyLevels() {
  return request({
    url: '/api/property/repair/urgency-levels',
    method: 'get'
  })
}

// 评价维修服务
export function evaluateRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/evaluate`,
    method: 'put',
    data
  })
}

// 验收维修工单
export function acceptCheckRepairOrder(orderId, data) {
  return request({
    url: `/api/property/repair/${orderId}/accept-check`,
    method: 'put',
    data
  })
}

// 批量分配维修工单
export function batchAssignRepairOrder(data) {
  return request({
    url: '/api/property/repair/batch-assign',
    method: 'put',
    data
  })
}