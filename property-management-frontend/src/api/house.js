import request from '@/utils/request'

// 获取房产列表
export function getHouseList(params) {
  return request({
    url: '/api/v1/property/house/list',
    method: 'get',
    params
  })
}

// 获取房产详情
export function getHouseDetail(houseId) {
  return request({
    url: `/api/v1/property/house/${houseId}`,
    method: 'get'
  })
}

// 新增房产
export function addHouse(data) {
  return request({
    url: '/api/v1/property/house',
    method: 'post',
    data
  })
}

// 编辑房产
export function updateHouse(data) {
  return request({
    url: '/api/v1/property/house',
    method: 'put',
    data
  })
}

// 删除房产
export function deleteHouse(houseId) {
  return request({
    url: `/api/v1/property/house/${houseId}`,
    method: 'delete'
  })
}

// 批量删除房产
export function batchDeleteHouse(houseIds) {
  return request({
    url: '/api/v1/property/house/batch',
    method: 'delete',
    data: houseIds
  })
}

// 获取房产统计信息
export function getHouseStats(params) {
  return request({
    url: '/api/v1/property/house/stats',
    method: 'get',
    params
  })
}

// 获取住户信息
export function getHouseResidents(houseId) {
  return request({
    url: `/api/v1/property/house/${houseId}/residents`,
    method: 'get'
  })
}

// 更新房产状态
export function updateHouseStatus(data) {
  return request({
    url: '/api/v1/property/house/status',
    method: 'put',
    data
  })
}

// 导出房产数据
export function exportHouseData(params) {
  return request({
    url: '/api/v1/property/house/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 根据单元获取房产列表
export function getHousesByUnit(unitId) {
  return request({
    url: `/api/v1/property/house/unit/${unitId}`,
    method: 'get'
  })
}