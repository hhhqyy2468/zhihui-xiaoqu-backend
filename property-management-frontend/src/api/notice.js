import request from '@/utils/request'

/**
 * 通知公告管理API接口
 */

// 获取通知公告列表
export function getNoticeList(params) {
  return request({
    url: '/api/property/notice/list',
    method: 'get',
    params
  })
}

// 获取通知公告详情
export function getNoticeDetail(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}`,
    method: 'get'
  })
}

// 新增通知公告
export function addNotice(data) {
  return request({
    url: '/api/property/notice',
    method: 'post',
    data
  })
}

// 更新通知公告
export function updateNotice(data) {
  return request({
    url: '/api/property/notice',
    method: 'put',
    data
  })
}

// 删除通知公告
export function deleteNotice(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}`,
    method: 'delete'
  })
}

// 批量删除通知公告
export function batchDeleteNotices(noticeIds) {
  return request({
    url: '/api/property/notice/batch',
    method: 'delete',
    data: { noticeIds }
  })
}

// 发布通知公告
export function publishNotice(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}/publish`,
    method: 'put'
  })
}

// 撤回通知公告
export function withdrawNotice(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}/withdraw`,
    method: 'put'
  })
}

// 置顶通知公告
export function pinNotice(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}/pin`,
    method: 'put'
  })
}

// 取消置顶
export function unpinNotice(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}/unpin`,
    method: 'put'
  })
}

// 获取我的通知公告
export function getMyNotices(params) {
  return request({
    url: '/api/property/notice/my-notices',
    method: 'get',
    params
  })
}

// 获取未读通知数量
export function getUnreadNoticeCount() {
  return request({
    url: '/api/property/notice/unread-count',
    method: 'get'
  })
}

// 标记通知为已读
export function markNoticeAsRead(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}/read`,
    method: 'put'
  })
}

// 批量标记为已读
export function batchMarkAsRead(noticeIds) {
  return request({
    url: '/api/property/notice/batch-read',
    method: 'put',
    data: { noticeIds }
  })
}

// 标记所有通知为已读
export function markAllAsRead() {
  return request({
    url: '/api/property/notice/mark-all-read',
    method: 'put'
  })
}

// 获取通知公告统计
export function getNoticeStats() {
  return request({
    url: '/api/property/notice/stats',
    method: 'get'
  })
}

// 获取通知公告分类
export function getNoticeCategories() {
  return request({
    url: '/api/property/notice/categories',
    method: 'get'
  })
}

// 根据分类获取通知公告
export function getNoticesByCategory(category, params) {
  return request({
    url: `/api/property/notice/category/${category}`,
    method: 'get',
    params
  })
}

// 上传通知附件
export function uploadNoticeAttachment(noticeId, files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  formData.append('businessType', 'notice')
  formData.append('businessId', noticeId)

  return request({
    url: `/api/property/notice/${noticeId}/attachments`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除通知附件
export function deleteNoticeAttachment(noticeId, attachmentId) {
  return request({
    url: `/api/property/notice/${noticeId}/attachment/${attachmentId}`,
    method: 'delete'
  })
}

// 获取通知附件
export function getNoticeAttachments(noticeId) {
  return request({
    url: `/api/property/notice/${noticeId}/attachments`,
    method: 'get'
  })
}

// 发送通知推送
export function sendNoticePush(noticeId, data) {
  return request({
    url: `/api/property/notice/${noticeId}/push`,
    method: 'post',
    data
  })
}

// 获取通知阅读记录
export function getNoticeReadRecords(noticeId, params) {
  return request({
    url: `/api/property/notice/${noticeId}/read-records`,
    method: 'get',
    params
  })
}

// 获取我的通知阅读记录
export function getMyNoticeReadRecords(params) {
  return request({
    url: '/api/property/notice/my-read-records',
    method: 'get',
    params
  })
}

// 导出通知公告
export function exportNotices(params) {
  return request({
    url: '/api/property/notice/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取通知公告趋势
export function getNoticeTrends(params) {
  return request({
    url: '/api/property/notice/trends',
    method: 'get',
    params
  })
}

// 复制通知公告
export function copyNotice(noticeId, data) {
  return request({
    url: `/api/property/notice/${noticeId}/copy`,
    method: 'post',
    data
  })
}

// 预览通知公告
export function previewNotice(data) {
  return request({
    url: '/api/property/notice/preview',
    method: 'post',
    data
  })
}

// 获取热门通知公告
export function getHotNotices(params) {
  return request({
    url: '/api/property/notice/hot',
    method: 'get',
    params
  })
}

// 获取最新通知公告
export function getLatestNotices(params) {
  return request({
    url: '/api/property/notice/latest',
    method: 'get',
    params
  })
}