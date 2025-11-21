import request from '@/utils/request'

// 获取房产列表
export function listHouses(params) {
  return request({
    url: '/property/house/list',
    method: 'get',
    params
  })
}

// 获取房产详情
export function getHouse(houseId) {
  return request({
    url: `/property/house/${houseId}`,
    method: 'get'
  })
}

// 新增房产
export function addHouse(data) {
  return request({
    url: '/property/house',
    method: 'post',
    data
  })
}

// 编辑房产
export function updateHouse(data) {
  return request({
    url: '/property/house',
    method: 'put',
    data
  })
}

// 删除房产
export function deleteHouse(houseId) {
  return request({
    url: `/property/house/${houseId}`,
    method: 'delete'
  })
}

// 批量删除房产
export function deleteHouses(houseIds) {
  return request({
    url: `/property/house/${houseIds.join(',')}`,
    method: 'delete'
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


// 根据用户名分配房产
export function assignHouseByUsername(data) {
  return request({
    url: '/property/house/assign-by-username',
    method: 'post',
    data
  })
}

// 根据用户名移除房产
export function removeHouseByUsername(data) {
  return request({
    url: '/property/house/remove-by-username',
    method: 'post',
    data
  })
}

// 获取楼栋列表
export function getBuildingOptions() {
  return request({
    url: '/property/building/list',
    method: 'get',
    params: { pageSize: 1000 } // 获取所有楼栋
  })
}

// 根据楼栋获取单元列表
export function getUnitOptions(buildingId) {
  return request({
    url: `/property/unit/by-building/${buildingId}`,
    method: 'get'
  })
}