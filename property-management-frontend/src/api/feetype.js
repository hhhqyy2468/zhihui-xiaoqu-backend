import request from '@/utils/request'

/**
 * 费用类型管理API接口
 */

// 获取费用类型列表
export function getFeeTypeList(params) {
  return request({
    url: '/api/property/feetype/list',
    method: 'get',
    params
  })
}

// 获取所有费用类型
export function getAllFeeTypes() {
  return request({
    url: '/api/property/feetype/all',
    method: 'get'
  })
}

// 获取费用类型详情
export function getFeeTypeDetail(feetypeId) {
  return request({
    url: `/api/property/feetype/${feetypeId}`,
    method: 'get'
  })
}

// 新增费用类型
export function addFeeType(data) {
  return request({
    url: '/api/property/feetype',
    method: 'post',
    data
  })
}

// 更新费用类型
export function updateFeeType(data) {
  return request({
    url: '/api/property/feetype',
    method: 'put',
    data
  })
}

// 删除费用类型
export function deleteFeeType(feetypeId) {
  return request({
    url: `/api/property/feetype/${feetypeId}`,
    method: 'delete'
  })
}

// 批量删除费用类型
export function batchDeleteFeeTypes(feetypeIds) {
  return request({
    url: '/api/property/feetype/batch',
    method: 'delete',
    data: { feetypeIds }
  })
}

// 获取费用类型统计
export function getFeeTypeStats() {
  return request({
    url: '/api/property/feetype/stats',
    method: 'get'
  })
}

// 更新费用类型状态
export function updateFeeTypeStatus(feetypeId, status) {
  return request({
    url: `/api/property/feetype/${feetypeId}/status`,
    method: 'put',
    data: { status }
  })
}

// 根据费用分类获取费用类型
export function getFeeTypesByCategory(category) {
  return request({
    url: `/api/property/feetype/category/${category}`,
    method: 'get'
  })
}

// 获取费用分类
export function getFeeCategories() {
  return request({
    url: '/api/property/feetype/categories',
    method: 'get'
  })
}

// 获取计费方式
export function getBillingMethods() {
  return request({
    url: '/api/property/feetype/billing-methods',
    method: 'get'
  })
}

// 计算费用
export function calculateFee(data) {
  return request({
    url: '/api/property/feetype/calculate',
    method: 'post',
    data
  })
}

// 复制费用类型
export function copyFeeType(feetypeId, data) {
  return request({
    url: `/api/property/feetype/${feetypeId}/copy`,
    method: 'post',
    data
  })
}

// 导入费用类型
export function importFeeTypes(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/api/property/feetype/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出费用类型
export function exportFeeTypes(params) {
  return request({
    url: '/api/property/feetype/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 验证费用类型名称唯一性
export function checkFeeTypeNameUnique(name, id) {
  return request({
    url: '/api/property/feetype/check-name',
    method: 'get',
    params: { name, id }
  })
}

// 获取费用类型的使用情况
export function getFeeTypeUsage(feetypeId) {
  return request({
    url: `/api/property/feetype/${feetypeId}/usage`,
    method: 'get'
  })
}

// 批量更新费用类型状态
export function batchUpdateFeeTypeStatus(data) {
  return request({
    url: '/api/property/feetype/batch-status',
    method: 'put',
    data
  })
}