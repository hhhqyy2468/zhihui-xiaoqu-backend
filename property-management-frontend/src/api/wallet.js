import request from '@/utils/request'

/**
 * 钱包管理API接口
 */

// 获取钱包列表
export function getWalletList(params) {
  return request({
    url: '/api/property/wallet/list',
    method: 'get',
    params
  })
}

// 获取钱包详情
export function getWalletDetail(walletId) {
  return request({
    url: `/api/property/wallet/${walletId}`,
    method: 'get'
  })
}

// 根据业主ID获取钱包
export function getWalletByOwnerId(ownerId) {
  return request({
    url: `/api/property/wallet/owner/${ownerId}`,
    method: 'get'
  })
}

// 获取我的钱包
export function getMyWallet() {
  return request({
    url: '/api/property/wallet/my',
    method: 'get'
  })
}

// 新增钱包
export function addWallet(data) {
  return request({
    url: '/api/property/wallet',
    method: 'post',
    data
  })
}

// 更新钱包
export function updateWallet(data) {
  return request({
    url: '/api/property/wallet',
    method: 'put',
    data
  })
}

// 删除钱包
export function deleteWallet(walletId) {
  return request({
    url: `/api/property/wallet/${walletId}`,
    method: 'delete'
  })
}

// 钱包充值
export function rechargeWallet(data) {
  return request({
    url: '/api/property/wallet/recharge',
    method: 'put',
    data
  })
}

// 钱包扣款
export function deductWallet(data) {
  return request({
    url: '/api/property/wallet/deduct',
    method: 'put',
    data
  })
}

// 调整钱包余额
export function adjustBalance(data) {
  return request({
    url: '/api/property/wallet/adjust-balance',
    method: 'put',
    data
  })
}

// 冻结钱包
export function freezeWallet(walletId) {
  return request({
    url: `/api/property/wallet/${walletId}/freeze`,
    method: 'put'
  })
}

// 解冻钱包
export function unfreezeWallet(walletId) {
  return request({
    url: `/api/property/wallet/${walletId}/unfreeze`,
    method: 'put'
  })
}

// 别名：解冻钱包
export const unfreeze = unfreezeWallet

// 检查余额是否充足
export function checkWalletBalance(ownerId, amount) {
  return request({
    url: `/api/property/wallet/check-balance`,
    method: 'get',
    params: { ownerId, amount }
  })
}

// 设置支付密码
export function setPayPassword(data) {
  return request({
    url: '/api/property/wallet/set-password',
    method: 'put',
    data
  })
}

// 修改支付密码
export function changePayPassword(data) {
  return request({
    url: '/api/property/wallet/change-password',
    method: 'put',
    data
  })
}

// 重置支付密码
export function resetPayPassword(walletId) {
  return request({
    url: `/api/property/wallet/${walletId}/reset-password`,
    method: 'put'
  })
}

// 别名：重置支付密码
export const resetPassword = resetPayPassword

// 验证支付密码
export function verifyPayPassword(data) {
  return request({
    url: '/api/property/wallet/verify-password',
    method: 'post',
    data
  })
}

// 获取钱包统计
export function getWalletStats(params) {
  return request({
    url: '/api/property/wallet/stats',
    method: 'get',
    params
  })
}

// 获取钱包交易记录
export function getWalletTransactions(params) {
  return request({
    url: '/api/property/wallet/transactions',
    method: 'get',
    params
  })
}

// 获取钱包充值记录
export function getWalletRechargeRecords(params) {
  return request({
    url: '/api/property/wallet/recharge-records',
    method: 'get',
    params
  })
}

// 获取钱包消费记录
export function getWalletConsumeRecords(params) {
  return request({
    url: '/api/property/wallet/consume-records',
    method: 'get',
    params
  })
}

// 批量冻结钱包
export function batchFreezeWallets(walletIds) {
  return request({
    url: '/api/property/wallet/batch-freeze',
    method: 'put',
    data: { walletIds }
  })
}

// 别名：批量冻结钱包
export const batchFreeze = batchFreezeWallets

// 批量解冻钱包
export function batchUnfreezeWallets(walletIds) {
  return request({
    url: '/api/property/wallet/batch-unfreeze',
    method: 'put',
    data: { walletIds }
  })
}

// 导出钱包数据
export function exportWallets(params) {
  return request({
    url: '/api/property/wallet/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 导出交易记录
export function exportTransactions(params) {
  return request({
    url: '/api/property/wallet/export-transactions',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取钱包余额变动趋势
export function getWalletBalanceTrends(params) {
  return request({
    url: '/api/property/wallet/balance-trends',
    method: 'get',
    params
  })
}

// 获取钱包使用情况分析
export function getWalletUsageAnalysis(params) {
  return request({
    url: '/api/property/wallet/usage-analysis',
    method: 'get',
    params
  })
}

// 钱包转账
export function transferWallet(data) {
  return request({
    url: '/api/property/wallet/transfer',
    method: 'put',
    data
  })
}

// 获取转账记录
export function getTransferRecords(params) {
  return request({
    url: '/api/property/wallet/transfer-records',
    method: 'get',
    params
  })
}