import request from '@/utils/request'

// 账单管理API

// 分页查询账单列表
export function getBillList(params) {
  return request({
    url: '/property/bill/page',
    method: 'get',
    params
  })
}

// 查询账单列表（不分页）
export function getAllBills(params) {
  return request({
    url: '/property/bill/list',
    method: 'get',
    params
  })
}

// 查询账单详情
export function getBillDetail(billId) {
  return request({
    url: `/property/bill/${billId}`,
    method: 'get'
  })
}

// 新增账单
export function createBill(data) {
  return request({
    url: '/property/bill',
    method: 'post',
    data
  })
}

// 修改账单
export function updateBill(data) {
  return request({
    url: '/property/bill',
    method: 'put',
    data
  })
}

// 删除账单
export function deleteBills(billIds) {
  return request({
    url: `/property/bill/${billIds}`,
    method: 'delete'
  })
}

// 生成账单
export function generateBills(data) {
  return request({
    url: '/property/bill/generate',
    method: 'post',
    data
  })
}

// 在线缴费
export function payBills(data) {
  return request({
    url: '/property/bill/pay',
    method: 'post',
    data
  })
}

// 更新超期账单状态
export function updateOverdueBills() {
  return request({
    url: '/property/bill/overdue',
    method: 'post'
  })
}

// 导出账单列表到Excel
export function exportBillsToExcel(params) {
  return request({
    url: '/property/bill/export',
    method: 'get',
    params,
    responseType: 'blob' // 设置响应类型为blob，用于文件下载
  })
}

// 批量导出账单到Excel
export function batchExportBills(data) {
  return request({
    url: '/property/bill/export/batch',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

// 批量打印账单
export function batchPrintBills(data) {
  return request({
    url: '/property/bill/print/batch',
    method: 'post',
    data
  })
}