import request from '@/utils/request'

// 获取楼栋列表
export function listBuildings(params) {
  return request({
    url: '/property/building/list',
    method: 'get',
    params
  })
}

// 获取楼栋详情
export function getBuilding(buildingId) {
  return request({
    url: `/property/building/${buildingId}`,
    method: 'get'
  })
}

// 新增楼栋
export function addBuilding(data) {
  return request({
    url: '/property/building',
    method: 'post',
    data
  })
}

// 编辑楼栋
export function updateBuilding(data) {
  return request({
    url: '/property/building',
    method: 'put',
    data
  })
}

// 删除楼栋
export function deleteBuilding(buildingId) {
  return request({
    url: `/property/building/${buildingId}`,
    method: 'delete'
  })
}

// 批量删除楼栋
export function deleteBuildings(buildingIds) {
  return request({
    url: `/property/building/${buildingIds.join(',')}`,
    method: 'delete'
  })
}