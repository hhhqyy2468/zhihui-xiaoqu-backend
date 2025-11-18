import request from '@/utils/request'

// 获取车位列表
export function listParkingSpaces(params) {
  return request({
    url: '/parking/space/list',
    method: 'get',
    params
  })
}

// 获取车位详情
export function getParkingSpace(spaceId) {
  return request({
    url: `/parking/space/${spaceId}`,
    method: 'get'
  })
}

// 新增车位
export function addParkingSpace(data) {
  return request({
    url: '/parking/space',
    method: 'post',
    data
  })
}

// 编辑车位
export function updateParkingSpace(data) {
  return request({
    url: '/parking/space',
    method: 'put',
    data
  })
}

// 删除车位
export function deleteParkingSpace(spaceId) {
  return request({
    url: `/parking/space/${spaceId}`,
    method: 'delete'
  })
}

// 批量删除车位
export function deleteParkingSpaces(spaceIds) {
  return request({
    url: `/parking/space/${spaceIds.join(',')}`,
    method: 'delete'
  })
}

// 获取车位统计数据
export function getParkingSpaceStats() {
  return request({
    url: '/parking/space/statistics',
    method: 'get'
  })
}