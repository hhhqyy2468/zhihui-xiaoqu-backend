import request from '@/utils/request'

// 查询钱包列表
export function listWallet(query) {
  return request({
    url: '/property/wallet/list',
    method: 'get',
    params: query
  })
}

// 获取钱包详细信息
export function getWallet(id) {
  return request({
    url: '/property/wallet/' + id,
    method: 'get'
  })
}

// 根据用户ID获取钱包
export function getWalletByUserId(userId) {
  return request({
    url: '/property/wallet/user/' + userId,
    method: 'get'
  })
}

// 新增钱包
export function addWallet(data) {
  return request({
    url: '/property/wallet',
    method: 'post',
    data: data
  })
}

// 修改钱包
export function updateWallet(data) {
  return request({
    url: '/property/wallet',
    method: 'put',
    data: data
  })
}

// 删除钱包
export function delWallet(id) {
  return request({
    url: '/property/wallet/' + id,
    method: 'delete'
  })
}

// 设置支付密码
export function setPayPassword(data) {
  return request({
    url: '/property/wallet/set-password',
    method: 'post',
    data: data
  })
}

// 修改支付密码
export function changePayPassword(data) {
  return request({
    url: '/property/wallet/change-password',
    method: 'post',
    data: data
  })
}

// 虚拟充值
export function virtualRecharge(data) {
  return request({
    url: '/property/wallet/virtual-recharge',
    method: 'post',
    data: data
  })
}

// 查询交易记录列表
export function listTransaction(query) {
  return request({
    url: '/property/wallet/transaction/list',
    method: 'get',
    params: query
  })
}

// 获取交易记录详细信息
export function getTransaction(id) {
  return request({
    url: '/property/wallet/transaction/' + id,
    method: 'get'
  })
}

// 批量充值（为所有业主充值）
export function batchRecharge(amount) {
  return request({
    url: '/property/wallet/batch-recharge',
    method: 'post',
    params: { amount }
  })
}

// 管理员充值（给指定用户充值）
export function adminRecharge(userId, amount) {
  return request({
    url: '/property/wallet/admin-recharge',
    method: 'post',
    params: { userId, amount }
  })
}

// 冻结钱包
export function freezeWallet(id) {
  return request({
    url: '/property/wallet/freeze/' + id,
    method: 'post'
  })
}

// 解冻钱包
export function unfreezeWallet(id) {
  return request({
    url: '/property/wallet/unfreeze/' + id,
    method: 'post'
  })
}