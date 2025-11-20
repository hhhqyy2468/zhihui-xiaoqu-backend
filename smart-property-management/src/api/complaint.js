import request from '@/utils/request'

// 获取投诉分页列表
export function getComplaintPage(params) {
  return request({
    url: '/property/complaint/list',
    method: 'get',
    params
  })
}

// 获取投诉详情
export function getComplaintDetail(id) {
  return request({
    url: `/property/complaint/${id}`,
    method: 'get'
  })
}

// 新增投诉
export function createComplaint(data) {
  return request({
    url: '/property/complaint',
    method: 'post',
    data
  })
}

// 修改投诉
export function updateComplaint(data) {
  return request({
    url: '/property/complaint',
    method: 'put',
    data
  })
}

// 删除投诉
export function deleteComplaint(ids) {
  return request({
    url: `/property/complaint/${ids}`,
    method: 'delete'
  })
}

// 投诉派单
export function assignComplaint(id, data) {
  return request({
    url: `/property/complaint/assign/${id}`,
    method: 'put',
    data
  })
}

// 处理投诉
export function handleComplaint(id, data) {
  return request({
    url: `/property/complaint/handle/${id}`,
    method: 'put',
    data
  })
}

// 评价投诉
export function rateComplaint(id, data) {
  return request({
    url: `/property/complaint/rate/${id}`,
    method: 'put',
    data
  })
}

// 关闭投诉
export function closeComplaint(id) {
  return request({
    url: `/property/complaint/close/${id}`,
    method: 'put'
  })
}

// 获取我的投诉列表
export function getMyComplaints(params) {
  return request({
    url: '/property/complaint/my',
    method: 'get',
    params
  })
}

// 上传投诉图片
export function uploadComplaintImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/property/complaint/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取投诉统计信息
export function getComplaintStats(params) {
  return request({
    url: '/property/complaint/stats',
    method: 'get',
    params
  })
}

// 获取投诉类型字典
export function getComplaintTypeDict() {
  return request({
    url: '/system/dict/data/type/complaint_type',
    method: 'get'
  })
}

// 获取投诉状态字典
export function getComplaintStatusDict() {
  return request({
    url: '/system/dict/data/type/complaint_status',
    method: 'get'
  })
}