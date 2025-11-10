import request from '@/utils/request'

// 获取楼栋列表
export function getBuildingList(params) {
  return request({
    url: '/building/list',
    method: 'get',
    params
  })
}

// 获取楼栋详情
export function getBuildingDetail(buildingId) {
  return request({
    url: `/building/${buildingId}`,
    method: 'get'
  })
}

// 新增楼栋
export function addBuilding(data) {
  return request({
    url: '/building',
    method: 'post',
    data
  })
}

// 编辑楼栋
export function updateBuilding(data) {
  return request({
    url: '/building',
    method: 'put',
    data
  })
}

// 删除楼栋
export function deleteBuilding(buildingId) {
  return request({
    url: `/building/${buildingId}`,
    method: 'delete'
  })
}

// 批量删除楼栋
export function batchDeleteBuilding(buildingIds) {
  return request({
    url: '/building/batch',
    method: 'delete',
    data: buildingIds
  })
}

// 获取楼栋统计信息
export function getBuildingStats() {
  return request({
    url: '/building/stats',
    method: 'get'
  })
}

// 导出楼栋数据
export function exportBuildingData(params) {
  return request({
    url: '/building/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
```