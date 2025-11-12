import request from '@/utils/request'

// 获取楼栋列表
export function getBuildingList(params) {
  return request({
    url: '/api/v1/property/building/list',
    method: 'get',
    params
  })
}

// 获取楼栋详情
export function getBuildingDetail(buildingId) {
  return request({
    url: `/api/v1/property/building/${buildingId}`,
    method: 'get'
  })
}

// 新增楼栋
export function addBuilding(data) {
  return request({
    url: '/api/v1/property/building',
    method: 'post',
    data
  })
}

// 编辑楼栋
export function updateBuilding(data) {
  return request({
    url: '/api/v1/property/building',
    method: 'put',
    data
  })
}

// 删除楼栋
export function deleteBuilding(buildingId) {
  return request({
    url: `/api/v1/property/building/${buildingId}`,
    method: 'delete'
  })
}

// 批量删除楼栋
export function batchDeleteBuilding(buildingIds) {
  return request({
    url: '/api/v1/property/building/batch',
    method: 'delete',
    data: buildingIds
  })
}

// 获取楼栋统计信息
export function getBuildingStats() {
  return request({
    url: '/api/v1/property/building/stats',
    method: 'get'
  })
}

// 导出楼栋数据
export function exportBuildingData(params) {
  return request({
    url: '/api/v1/property/building/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 统一导出
const buildingApi = {
  getList: getBuildingList,
  getDetail: getBuildingDetail,
  create: addBuilding,
  update: updateBuilding,
  delete: deleteBuilding,
  batchDelete: batchDeleteBuilding,
  getStatistics: getBuildingStats,
  export: exportBuildingData
}

export default buildingApi