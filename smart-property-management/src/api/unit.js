import request from '@/utils/request'

// 获取单元列表
export function listUnits(params) {
  return request({
    url: '/property/unit/list',
    method: 'get',
    params
  })
}

// 获取单元详情
export function getUnit(unitId) {
  return request({
    url: `/property/unit/${unitId}`,
    method: 'get'
  })
}

// 新增单元
export function addUnit(data) {
  return request({
    url: '/property/unit',
    method: 'post',
    data
  })
}

// 编辑单元
export function updateUnit(data) {
  return request({
    url: '/property/unit',
    method: 'put',
    data
  })
}

// 删除单元
export function deleteUnit(unitId) {
  return request({
    url: `/property/unit/${unitId}`,
    method: 'delete'
  })
}

// 批量删除单元
export function deleteUnits(unitIds) {
  return request({
    url: `/property/unit/${unitIds.join(',')}`,
    method: 'delete'
  })
}

// 根据楼栋获取单元列表
export function getUnitsByBuilding(buildingId) {
  return request({
    url: `/property/unit/by-building/${buildingId}`,
    method: 'get'
  })
}