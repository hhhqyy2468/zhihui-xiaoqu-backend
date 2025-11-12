import request from '@/utils/request'

/**
 * 账单管理API接口
 */

// 获取账单列表
export function getBillList(params) {
  return request({
    url: '/api/property/bill/list',
    method: 'get',
    params
  })
}

// 获取账单详情
export function getBillDetail(billId) {
  return request({
    url: `/api/property/bill/${billId}`,
    method: 'get'
  })
}

// 新增账单
export function addBill(data) {
  return request({
    url: '/api/property/bill',
    method: 'post',
    data
  })
}

// 更新账单
export function updateBill(data) {
  return request({
    url: '/api/property/bill',
    method: 'put',
    data
  })
}

// 删除账单
export function deleteBill(billId) {
  return request({
    url: `/api/property/bill/${billId}`,
    method: 'delete'
  })
}

// 批量删除账单
export function batchDeleteBills(billIds) {
  return request({
    url: '/api/property/bill/batch',
    method: 'delete',
    data: { billIds }
  })
}

// 批量生成账单
export function generateBills(data) {
  return request({
    url: '/api/property/bill/generate',
    method: 'post',
    data
  })
}

// 支付账单
export function payBill(billId, data) {
  return request({
    url: `/api/property/bill/${billId}/pay`,
    method: 'put',
    data
  })
}

// 批量支付账单
export function payBills(data) {
  return request({
    url: '/api/property/bill/pay-batch',
    method: 'put',
    data
  })
}

// 获取账单统计
export function getBillStats(params) {
  return request({
    url: '/api/property/bill/stats',
    method: 'get',
    params
  })
}

// 获取业主账单
export function getOwnerBills(ownerId, params) {
  return request({
    url: `/api/property/bill/owner/${ownerId}`,
    method: 'get',
    params
  })
}

// 获取我的账单
export function getMyBills(params) {
  return request({
    url: '/api/property/bill/my-bills',
    method: 'get',
    params
  })
}

// 获取逾期账单
export function getOverdueBills(params) {
  return request({
    url: '/api/property/bill/overdue',
    method: 'get',
    params
  })
}

// 更新账单状态
export function updateBillStatus(billId, status) {
  return request({
    url: `/api/property/bill/${billId}/status`,
    method: 'put',
    data: { status }
  })
}

// 批量更新账单状态
export function batchUpdateBillStatus(data) {
  return request({
    url: '/api/property/bill/batch-status',
    method: 'put',
    data
  })
}

// 发送账单通知
export function sendBillNotification(billId) {
  return request({
    url: `/api/property/bill/${billId}/notify`,
    method: 'post'
  })
}

// 批量发送账单通知
export function batchSendBillNotifications(billIds) {
  return request({
    url: '/api/property/bill/batch-notify',
    method: 'post',
    data: { billIds }
  })
}

// 获取账单支付记录
export function getBillPaymentRecords(billId) {
  return request({
    url: `/api/property/bill/${billId}/payments`,
    method: 'get'
  })
}

// 生成账单PDF
export function generateBillPDF(billId) {
  return request({
    url: `/api/property/bill/${billId}/pdf`,
    method: 'get',
    responseType: 'blob'
  })
}

// 导出账单
export function exportBills(params) {
  return request({
    url: '/api/property/bill/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 导入账单
export function importBills(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/api/property/bill/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取账单汇总信息
export function getBillSummary(params) {
  return request({
    url: '/api/property/bill/summary',
    method: 'get',
    params
  })
}

// 催缴账单
export function remindBill(billId) {
  return request({
    url: `/api/property/bill/${billId}/remind`,
    method: 'post'
  })
}

// 批量催缴账单
export function batchRemindBills(billIds) {
  return request({
    url: '/api/property/bill/batch-remind',
    method: 'post',
    data: { billIds }
  })
}

// 获取账单趋势分析
export function getBillTrends(params) {
  return request({
    url: '/api/property/bill/trends',
    method: 'get',
    params
  })
}