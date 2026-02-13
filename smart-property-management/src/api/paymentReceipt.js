import request from '@/utils/request'

// 缴费收据API

// 分页查询收据列表
export function getReceiptList(params) {
  return request({
    url: '/property/payment-receipt/page',
    method: 'get',
    params
  })
}

// 查询收据列表（不分页）
export function getAllReceipts(params) {
  return request({
    url: '/property/payment-receipt/list',
    method: 'get',
    params
  })
}

// 查询收据详情
export function getReceiptDetail(receiptId) {
  return request({
    url: `/property/payment-receipt/${receiptId}`,
    method: 'get'
  })
}

// 根据账单ID查询收据
export function getReceiptByBillId(billId) {
  return request({
    url: `/property/payment-receipt/bill/${billId}`,
    method: 'get'
  })
}

// 获取我的收据列表
export function getMyReceiptList(params) {
  return request({
    url: '/property/payment-receipt/my/list',
    method: 'get',
    params
  })
}
