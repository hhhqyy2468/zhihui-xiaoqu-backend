import request from '@/utils/request'

// 获取费用类型分页列表
export function getFeeTypePage(params) {
  return request({
    url: '/property/feetype/page',
    method: 'get',
    params
  })
}

// 获取费用类型列表
export function getFeeTypeList(params) {
  return request({
    url: '/property/feetype/list',
    method: 'get',
    params
  })
}

// 获取费用类型详情
export function getFeeTypeDetail(feeTypeId) {
  return request({
    url: `/property/feetype/${feeTypeId}`,
    method: 'get'
  })
}

// 新增费用类型
export function createFeeType(data) {
  return request({
    url: '/property/feetype',
    method: 'post',
    data
  })
}

// 修改费用类型
export function updateFeeType(data) {
  return request({
    url: '/property/feetype',
    method: 'put',
    data
  })
}

// 删除费用类型
export function deleteFeeType(feeTypeIds) {
  return request({
    url: `/property/feetype/${feeTypeIds}`,
    method: 'delete'
  })
}

// 获取所有启用的费用类型（下拉框用）
export function getAllFeeTypes() {
  return request({
    url: '/property/feetype/all',
    method: 'get'
  })
}