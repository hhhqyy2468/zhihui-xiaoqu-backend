import request from '@/utils/request'

// 获取用户房产关联列表
export function listUserHouses(params) {
  return request({
    url: '/property/user-house/list',
    method: 'get',
    params
  })
}

// 根据用户ID查询房产列表
export function getHousesByUserId(userId) {
  return request({
    url: `/property/user-house/user/${userId}`,
    method: 'get'
  })
}

// 获取用户房产关联详细信息
export function getUserHouse(id) {
  return request({
    url: `/property/user-house/${id}`,
    method: 'get'
  })
}

// 分配房产给用户
export function assignHouse(data) {
  return request({
    url: '/property/user-house/assign',
    method: 'post',
    data
  })
}

// 取消用户房产关联
export function unassignHouse(data) {
  return request({
    url: '/property/user-house/unassign',
    method: 'delete',
    data
  })
}