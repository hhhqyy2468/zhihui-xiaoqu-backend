import request from '@/utils/request'

/**
 * 业主管理API接口
 */

// 获取业主列表
export function getOwnerList(params) {
  return request({
    url: '/api/property/owner/list',
    method: 'get',
    params
  })
}

// 获取业主详情
export function getOwnerDetail(ownerId) {
  return request({
    url: `/api/property/owner/${ownerId}`,
    method: 'get'
  })
}

// 新增业主
export function addOwner(data) {
  return request({
    url: '/api/property/owner',
    method: 'post',
    data
  })
}

// 更新业主信息
export function updateOwner(data) {
  return request({
    url: '/api/property/owner',
    method: 'put',
    data
  })
}

// 删除业主
export function deleteOwner(ownerId) {
  return request({
    url: `/api/property/owner/${ownerId}`,
    method: 'delete'
  })
}

// 批量删除业主
export function batchDeleteOwners(ownerIds) {
  return request({
    url: '/api/property/owner/batch',
    method: 'delete',
    data: { ownerIds }
  })
}

// 获取业主统计信息
export function getOwnerStats() {
  return request({
    url: '/api/property/owner/stats',
    method: 'get'
  })
}

// 根据手机号查询业主
export function getOwnerByPhone(phone) {
  return request({
    url: `/api/property/owner/phone/${phone}`,
    method: 'get'
  })
}

// 根据身份证号查询业主
export function getOwnerByIdCard(idCard) {
  return request({
    url: `/api/property/owner/idcard/${idCard}`,
    method: 'get'
  })
}

// 获取业主的房产信息
export function getOwnerProperties(ownerId) {
  return request({
    url: `/api/property/owner/${ownerId}/properties`,
    method: 'get'
  })
}

// 获取业主的车位信息
export function getOwnerParkingSpaces(ownerId) {
  return request({
    url: `/api/property/owner/${ownerId}/parking-spaces`,
    method: 'get'
  })
}

// 获取业主的账单信息
export function getOwnerBills(ownerId, params) {
  return request({
    url: `/api/property/owner/${ownerId}/bills`,
    method: 'get',
    params
  })
}

// 获取业主的维修记录
export function getOwnerRepairRecords(ownerId, params) {
  return request({
    url: `/api/property/owner/${ownerId}/repair-records`,
    method: 'get',
    params
  })
}

// 导入业主信息
export function importOwners(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/api/property/owner/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出业主信息
export function exportOwners(params) {
  return request({
    url: '/api/property/owner/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 更新业主状态
export function updateOwnerStatus(ownerId, status) {
  return request({
    url: `/api/property/owner/${ownerId}/status`,
    method: 'put',
    data: { status }
  })
}

// 重置业主密码
export function resetOwnerPassword(ownerId) {
  return request({
    url: `/api/property/owner/${ownerId}/reset-password`,
    method: 'put'
  })
}

// 统一导出
const ownerApi = {
  getList: getOwnerList,
  getDetail: getOwnerDetail,
  create: addOwner,
  update: updateOwner,
  delete: deleteOwner,
  batchDelete: batchDeleteOwners,
  getStatistics: getOwnerStats,
  getByPhone: getOwnerByPhone,
  getByIdCard: getOwnerByIdCard,
  getProperties: getOwnerProperties,
  getParkingSpaces: getOwnerParkingSpaces,
  getBills: getOwnerBills,
  getRepairRecords: getOwnerRepairRecords,
  import: importOwners,
  export: exportOwners,
  updateStatus: updateOwnerStatus,
  resetPassword: resetOwnerPassword
}

export default ownerApi