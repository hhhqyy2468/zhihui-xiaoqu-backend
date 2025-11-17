import request from '@/utils/request'

// 获取公告列表
export function getNoticeList(params) {
  return request({
    url: '/property/notice/list',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getNoticeDetail(id) {
  return request({
    url: `/property/notice/${id}`,
    method: 'get'
  })
}

// 新增公告
export function addNotice(data) {
  return request({
    url: '/property/notice',
    method: 'post',
    data
  })
}

// 更新公告
export function updateNotice(data) {
  return request({
    url: '/property/notice',
    method: 'put',
    data
  })
}

// 删除公告
export function deleteNotice(ids) {
  return request({
    url: `/property/notice/${ids}`,
    method: 'delete'
  })
}

// 发布公告
export function publishNotice(id) {
  return request({
    url: `/property/notice/${id}/publish`,
    method: 'put'
  })
}

// 设置公告置顶状态
export function setNoticeTop(id, isTop) {
  return request({
    url: `/property/notice/${id}/top`,
    method: 'put',
    params: { isTop }
  })
}

// 撤回公告
export function withdrawNotice(id) {
  return request({
    url: `/property/notice/${id}/withdraw`,
    method: 'put'
  })
}

// 获取用户可见的公告列表
export function getUserNotices(params) {
  return request({
    url: '/property/notice/user',
    method: 'get',
    params
  })
}

// 标记公告已读
export function markNoticeAsRead(id) {
  return request({
    url: `/property/notice/${id}/read`,
    method: 'post'
  })
}

// 获取公告统计信息
export function getNoticeStats(id) {
  return request({
    url: `/property/notice/${id}/stats`,
    method: 'get'
  })
}

// 获取公告概览统计
export function getNoticeOverviewStats() {
  return request({
    url: '/property/notice/stats/overview',
    method: 'get'
  })
}

// 获取楼栋列表（用于公告发布范围选择）
export function getBuildingList() {
  return request({
    url: '/property/building/list',
    method: 'get',
    params: { pageSize: 1000 }
  })
}

// 获取单元列表（用于公告发布范围选择）
export function getUnitList(buildingId) {
  return request({
    url: `/property/unit/by-building/${buildingId}`,
    method: 'get'
  })
}

// 获取公告管理统计数据
export function getNoticeStatistics() {
  return request({
    url: '/property/notice/statistics',
    method: 'get'
  })
}