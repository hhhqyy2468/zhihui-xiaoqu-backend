import request from '@/utils/request'

// 获取单元列表
export function getUnitList(params) {
  return request({
    url: '/api/v1/property/unit/list',
    method: 'get',
    params
  })
}

// 获取单元详情
export function getUnitDetail(unitId) {
  return request({
    url: `/api/v1/property/unit/${unitId}`,
    method: 'get'
  })
}

// 新增单元
export function addUnit(data) {
  return request({
    url: '/api/v1/property/unit',
    method: 'post',
    data
  })
}

// 编辑单元
export function updateUnit(data) {
  return request({
    url: '/api/v1/property/unit',
    method: 'put',
    data
  })
}

// 删除单元
export function deleteUnit(unitId) {
  return request({
    url: `/api/v1/property/unit/${unitId}`,
    method: 'delete'
  })
}

// 批量删除单元
export function batchDeleteUnit(unitIds) {
  return request({
    url: '/api/v1/property/unit/batch',
    method: 'delete',
    data: unitIds
  })
}

// 根据楼栋获取单元列表
export function getUnitsByBuilding(buildingId) {
  return request({
    url: `/api/v1/property/unit/by-building/${buildingId}`,
    method: 'get'
  })
}

// 获取单元统计信息
export function getUnitStats(unitId) {
  return request({
    url: `/api/v1/property/unit/${unitId}/stats`,
    method: 'get'
  })
}