import request from '@/utils/request'

// 获取业主列表
export function listOwners(params) {
  return request({
    url: '/property/owner/list',
    method: 'get',
    params
  })
}

// 获取业主详情
export function getOwner(ownerId) {
  return request({
    url: `/property/owner/${ownerId}`,
    method: 'get'
  })
}

// 新增业主
export function addOwner(data) {
  return request({
    url: '/property/owner',
    method: 'post',
    data
  })
}

// 编辑业主
export function updateOwner(data) {
  return request({
    url: '/property/owner',
    method: 'put',
    data
  })
}

// 删除业主
export function deleteOwner(ownerId) {
  return request({
    url: `/property/owner/${ownerId}`,
    method: 'delete'
  })
}

// 批量删除业主
export function deleteOwners(ownerIds) {
  return request({
    url: `/property/owner/${ownerIds.join(',')}`,
    method: 'delete'
  })
}

// 重置业主密码
export function resetPassword(data) {
  return request({
    url: '/property/owner/reset-password',
    method: 'put',
    data
  })
}

// 修改业主状态
export function changeStatus(data) {
  return request({
    url: '/property/owner/change-status',
    method: 'put',
    data
  })
}

// 根据房产ID获取业主列表
export function getOwnersByHouse(houseId) {
  return request({
    url: `/property/owner/by-house/${houseId}`,
    method: 'get'
  })
}

// 导出业主数据
export function exportOwners(data) {
  return request({
    url: '/property/owner/export',
    method: 'post',
    data
  })
}

// 导入业主数据
export function importOwners(file, updateSupport) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('updateSupport', updateSupport)
  return request({
    url: '/property/owner/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取导入模板
export function downloadTemplate() {
  return request({
    url: '/property/owner/import-template',
    method: 'get'
  })
}