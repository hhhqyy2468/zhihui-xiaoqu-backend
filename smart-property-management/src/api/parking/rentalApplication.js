import request from '@/utils/request'

// 查询租赁申请列表
export function listApplication(query) {
  return request({
    url: '/parking/rental/application/list',
    method: 'get',
    params: query
  })
}

// 查询租赁申请详细
export function getApplication(id) {
  return request({
    url: '/parking/rental/application/' + id,
    method: 'get'
  })
}

// 新增租赁申请
export function addApplication(data) {
  return request({
    url: '/parking/rental/application',
    method: 'post',
    data: data
  })
}

// 修改租赁申请
export function updateApplication(data) {
  return request({
    url: '/parking/rental/application',
    method: 'put',
    data: data
  })
}

// 删除租赁申请
export function delApplication(id) {
  return request({
    url: '/parking/rental/application/' + id,
    method: 'delete'
  })
}

// 审核租赁申请
export function reviewApplication(id, status, reviewRemark) {
  return request({
    url: '/parking/rental/application/' + id + '/review',
    method: 'put',
    params: {
      status: status,
      reviewRemark: reviewRemark
    }
  })
}

// 查询待审核申请列表
export function getPendingApplications() {
  return request({
    url: '/parking/rental/application/pending',
    method: 'get'
  })
}