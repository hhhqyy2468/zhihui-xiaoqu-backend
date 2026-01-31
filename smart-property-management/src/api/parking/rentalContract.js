import request from '@/utils/request'

// 查询租赁合同列表
export function listContract(query) {
  return request({
    url: '/parking/rental/contract/list',
    method: 'get',
    params: query
  })
}

// 查询租赁合同详细
export function getContract(id) {
  return request({
    url: '/parking/rental/contract/' + id,
    method: 'get'
  })
}

// 根据申请ID生成合同
export function generateContract(applicationId) {
  return request({
    url: '/parking/rental/contract/generate/' + applicationId,
    method: 'post'
  })
}

// 终止合同
export function terminateContract(id, terminateReason) {
  return request({
    url: '/parking/rental/contract/terminate/' + id,
    method: 'put',
    params: { terminateReason }
  })
}

// 获取合同统计数据
export function getContractStats() {
  return request({
    url: '/parking/rental/contract/statistics',
    method: 'get'
  })
}

// 查询即将到期的合同
export function getExpiringContracts(days = 7) {
  return request({
    url: '/parking/rental/contract/expiring',
    method: 'get',
    params: { days }
  })
}

// 查询我的车位（当前用户的租赁合同）
export function getMyContracts() {
  return request({
    url: '/parking/rental/contract/my',
    method: 'get'
  })
}
