import request from '@/utils/request'

// 用户管理接口
export function listUsers(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function delUser(userIds) {
  return request({
    url: `/system/user/${userIds}`,
    method: 'delete'
  })
}

export function resetUserPwd(userId, password) {
  const data = {
    userId,
    password
  }
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data
  })
}

export function changeUserStatus(data) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data
  })
}


// 字典类型接口
export function listDictTypes(params) {
  return request({
    url: 'system/dict/type/list',
    method: 'get',
    params
  })
}

export function getDictType(dictId) {
  return request({
    url: `system/dict/type/${dictId}`,
    method: 'get'
  })
}

export function addDictType(data) {
  return request({
    url: 'system/dict/type',
    method: 'post',
    data
  })
}

export function updateDictType(data) {
  return request({
    url: 'system/dict/type',
    method: 'put',
    data
  })
}

export function delDictType(dictIds) {
  return request({
    url: `system/dict/type/${dictIds}`,
    method: 'delete'
  })
}

export function refreshCache(dictType) {
  return request({
    url: 'system/dict/type/refreshCache',
    method: 'delete',
    params: {
      dictType
    }
  })
}

export function getDictsByType(dictType) {
  return request({
    url: `system/dict/type/getDictsByType/${dictType}`,
    method: 'get'
  })
}

// 字典数据接口
export function listDictData(params) {
  return request({
    url: 'system/dict/data/list',
    method: 'get',
    params
  })
}

export function getDictData(dictCode) {
  return request({
    url: `system/dict/data/${dictCode}`,
    method: 'get'
  })
}

export function addDictData(data) {
  return request({
    url: 'system/dict/data',
    method: 'post',
    data
  })
}

export function updateDictData(data) {
  return request({
    url: 'system/dict/data',
    method: 'put',
    data
  })
}

export function delDictData(dictCodes) {
  return request({
    url: `system/dict/data/${dictCodes}`,
    method: 'delete'
  })
}

// 用户统一操作接口
export function userOperation(data) {
  return request({
    url: '/system/user/operation',
    method: 'post',
    data
  })
}

// 获取用户角色列表
export function getUserRoles(userId) {
  return request({
    url: `/system/user/${userId}/roles`,
    method: 'get'
  })
}


// 角色管理接口（修复URL）
export function listRoles(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getRole(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function delRole(roleIds) {
  return request({
    url: `/system/role/${roleIds}`,
    method: 'delete'
  })
}

export function changeRoleStatus(data) {
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data
  })
}