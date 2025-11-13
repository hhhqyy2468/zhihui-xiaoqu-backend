import request from '@/utils/request'

// 获取房产列表
export function getHouseList(params) {
  return request({
    url: '/house/list',
    method: 'get',
    params
  })
}

// 获取房产详情
export function getHouseDetail(houseId) {
  return request({
    url: `/house/${houseId}`,
    method: 'get'
  })
}

// 新增房产
export function addHouse(data) {
  return request({
    url: '/house',
    method: 'post',
    data
  })
}

// 编辑房产
export function updateHouse(data) {
  return request({
    url: '/house',
    method: 'put',
    data
  })
}

// 删除房产
export function deleteHouse(houseId) {
  return request({
    url: `/house/${houseId}`,
    method: 'delete'
  })
}

// 批量删除房产
export function batchDeleteHouse(houseIds) {
  return request({
    url: '/house/batch',
    method: 'delete',
    data: houseIds
  })
}

// 获取房产统计信息
export function getHouseStats(params) {
  return request({
    url: '/house/stats',
    method: 'get',
    params
  })
}

// 获取住户信息
export function getHouseResidents(houseId) {
  return request({
    url: `/house/${houseId}/residents`,
    method: 'get'
  })
}

// 更新房产状态
export function updateHouseStatus(data) {
  return request({
    url: '/house/status',
    method: 'put',
    data
  })
}

// 导出房产数据
export function exportHouseData(params) {
  return request({
    url: '/house/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}