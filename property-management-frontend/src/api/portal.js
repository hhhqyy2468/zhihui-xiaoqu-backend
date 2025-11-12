import request from '@/utils/request'

/**
 * 业主门户API接口
 */

// 获取门户首页数据
export function getPortalDashboard() {
  return request({
    url: '/api/portal/dashboard',
    method: 'get'
  })
}

// 获取小区信息
export function getCommunityInfo() {
  return request({
    url: '/api/portal/community/info',
    method: 'get'
  })
}

// 获取小区公告
export function getCommunityNotices(params) {
  return request({
    url: '/api/portal/community/notices',
    method: 'get',
    params
  })
}

// 获取便民服务
export function getConvenienceServices() {
  return request({
    url: '/api/portal/services',
    method: 'get'
  })
}

// 获取联系方式
export function getContactInfo() {
  return request({
    url: '/api/portal/contact',
    method: 'get'
  })
}

// 提交投诉建议
export function submitComplaint(data) {
  return request({
    url: '/api/portal/complaint',
    method: 'post',
    data
  })
}

// 获取我的投诉记录
export function getMyComplaints(params) {
  return request({
    url: '/api/portal/complaint/my-list',
    method: 'get',
    params
  })
}

// 提交报修申请
export function submitRepairRequest(data) {
  return request({
    url: '/api/portal/repair/request',
    method: 'post',
    data
  })
}

// 获取我的报修记录
export function getMyRepairRequests(params) {
  return request({
    url: '/api/portal/repair/my-requests',
    method: 'get',
    params
  })
}

// 获取我的账单
export function getMyBills(params) {
  return request({
    url: '/api/portal/bill/my-bills',
    method: 'get',
    params
  })
}

// 支付账单
export function payBill(billId, data) {
  return request({
    url: `/api/portal/bill/${billId}/pay`,
    method: 'put',
    data
  })
}

// 获取我的钱包信息
export function getMyWalletInfo() {
  return request({
    url: '/api/portal/wallet/info',
    method: 'get'
  })
}

// 钱包充值
export function rechargeMyWallet(data) {
  return request({
    url: '/api/portal/wallet/recharge',
    method: 'put',
    data
  })
}

// 获取钱包交易记录
export function getMyWalletTransactions(params) {
  return request({
    url: '/api/portal/wallet/transactions',
    method: 'get',
    params
  })
}

// 获取我的房产信息
export function getMyProperties() {
  return request({
    url: '/api/portal/property/my-properties',
    method: 'get'
  })
}

// 获取我的车位信息
export function getMyParkingSpaces() {
  return request({
    url: '/api/portal/parking/my-spaces',
    method: 'get'
  })
}

// 更新个人信息
export function updateMyProfile(data) {
  return request({
    url: '/api/portal/profile/update',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/portal/profile/change-password',
    method: 'put',
    data
  })
}

// 上传头像
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', 'avatar')

  return request({
    url: '/api/portal/profile/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取消息通知
export function getMyNotifications(params) {
  return request({
    url: '/api/portal/notification/my-notifications',
    method: 'get',
    params
  })
}

// 标记消息为已读
export function markNotificationAsRead(notificationId) {
  return request({
    url: `/api/portal/notification/${notificationId}/read`,
    method: 'put'
  })
}

// 获取访客预约记录
export function getMyVisitorRecords(params) {
  return request({
    url: '/api/portal/visitor/my-records',
    method: 'get',
    params
  })
}

// 提交访客预约
export function submitVisitorAppointment(data) {
  return request({
    url: '/api/portal/visitor/appointment',
    method: 'post',
    data
  })
}