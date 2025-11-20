import request from '@/utils/request'

// 获取维修工单分页列表
export function getRepairOrderPage(params) {
  return request({
    url: '/property/repair/list',
    method: 'get',
    params
  })
}

// 获取维修工单详情
export function getRepairOrderDetail(id) {
  return request({
    url: `/property/repair/${id}`,
    method: 'get'
  })
}

// 新增维修工单
export function createRepairOrder(data) {
  return request({
    url: '/property/repair',
    method: 'post',
    data
  })
}

// 修改维修工单
export function updateRepairOrder(data) {
  return request({
    url: '/property/repair',
    method: 'put',
    data
  })
}

// 删除维修工单
export function deleteRepairOrder(ids) {
  return request({
    url: `/property/repair/${ids}`,
    method: 'delete'
  })
}

// 获取维修人员列表
export function getRepairers() {
  return request({
    url: '/property/repair/repairers',
    method: 'get'
  })
}

// 维修人员查看自己的工单
export function getMyWorkerOrders(params) {
  return request({
    url: '/property/repair/worker/my-orders',
    method: 'get',
    params
  })
}

// 派工维修工单
export function assignRepairOrder(id, data) {
  return request({
    url: `/property/repair/assign/${id}`,
    method: 'put',
    data
  })
}

// 接单
export function acceptRepairOrder(id) {
  return request({
    url: `/property/repair/accept/${id}`,
    method: 'put'
  })
}

// 完成维修
export function completeRepairOrder(id, data) {
  return request({
    url: `/property/repair/complete/${id}`,
    method: 'put',
    data
  })
}

// 维修师傅提交维修记录
export function handleRepairOrder(id, data) {
  return request({
    url: `/property/repair/handle/${id}`,
    method: 'put',
    data
  })
}

// 验收维修
export function inspectRepairOrder(id, data) {
  return request({
    url: `/property/repair/inspect/${id}`,
    method: 'put',
    data
  })
}

// 评价维修
export function rateRepairOrder(id, data) {
  return request({
    url: `/property/repair/rate/${id}`,
    method: 'put',
    data
  })
}

// 重新派工
export function reassignRepairOrder(id, data) {
  return request({
    url: `/property/repair/reassign/${id}`,
    method: 'put',
    data
  })
}

// 取消工单
export function cancelRepairOrder(id, data) {
  return request({
    url: `/property/repair/cancel/${id}`,
    method: 'put',
    data
  })
}

// 获取维修统计
export function getRepairStats(params) {
  return request({
    url: '/property/repair/stats',
    method: 'get',
    params
  })
}

// 获取维修人员列表
export function getRepairerList() {
  return request({
    url: '/property/repair/repairer/list',
    method: 'get'
  })
}

// 获取维修类型列表
export function getRepairTypeList() {
  return request({
    url: '/property/repair/type/list',
    method: 'get'
  })
}

// 获取工单统计
export function getOrderStats(params) {
  return request({
    url: '/property/repair/stats/summary',
    method: 'get',
    params
  })
}

// 获取维修人员工作量统计
export function getWorkerStats(params) {
  return request({
    url: '/property/repair/worker/stats',
    method: 'get',
    params
  })
}

// 导出维修工单
export function exportRepairOrders(params) {
  return request({
    url: '/property/repair/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取工单状态统计
export function getStatusStats() {
  return request({
    url: '/property/repair/stats/status',
    method: 'get'
  })
}

// 获取维修类型统计
export function getTypeStats() {
  return request({
    url: '/property/repair/stats/type',
    method: 'get'
  })
}

// 获取紧急程度统计
export function getUrgencyStats() {
  return request({
    url: '/property/repair/stats/urgency',
    method: 'get'
  })
}

// 获取工单进度统计
export function getProgressStats() {
  return request({
    url: '/property/repair/stats/progress',
    method: 'get'
  })
}

// 获取费用统计
export function getCostStats(params) {
  return request({
    url: '/property/repair/stats/cost',
    method: 'get',
    params
  })
}

// 获取评价统计
export function getRatingStats() {
  return request({
    url: '/property/repair/stats/rating',
    method: 'get'
  })
}

// 业主报修 - 创建维修工单
export function ownerCreateRepairOrder(data) {
  return request({
    url: '/property/repair/owner',
    method: 'post',
    data
  })
}

// 业主查看我的报修
export function getMyRepairOrders(params) {
  return request({
    url: '/property/repair/owner/my-repairs',
    method: 'get',
    params
  })
}

// 业主删除自己的报修工单
export function ownerDeleteRepairOrder(id) {
  return request({
    url: `/property/repair/owner/${id}`,
    method: 'delete'
  })
}

// 上传维修图片
export function uploadRepairImages(formData) {
  return request({
    url: '/property/repair/upload-images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 系统管理员归档维修工单
export function archiveRepairOrder(id) {
  return request({
    url: `/property/repair/archive/${id}`,
    method: 'put'
  })
}

// 批量归档维修工单
export function batchArchiveRepairOrders(ids) {
  return request({
    url: '/property/repair/archive/batch',
    method: 'put',
    data: ids
  })
}

// 获取归档维修工单列表
export function getArchivedRepairOrders(params) {
  return request({
    url: '/property/repair/archive/list',
    method: 'get',
    params
  })
}