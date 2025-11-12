import request from '@/utils/request'

/**
 * 系统管理API接口
 */

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(userId) {
  return request({
    url: `/api/system/user/${userId}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/api/system/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(userId) {
  return request({
    url: `/api/system/user/${userId}`,
    method: 'delete'
  })
}

// 重置用户密码
export function resetUserPassword(userId) {
  return request({
    url: `/api/system/user/${userId}/reset-password`,
    method: 'put'
  })
}

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/api/system/role/list',
    method: 'get',
    params
  })
}

// 获取所有角色
export function getAllRoles() {
  return request({
    url: '/api/system/role/all',
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/api/system/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(data) {
  return request({
    url: '/api/system/role',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(roleId) {
  return request({
    url: `/api/system/role/${roleId}`,
    method: 'delete'
  })
}

// 获取菜单列表
export function getMenuList(params) {
  return request({
    url: '/api/system/menu/list',
    method: 'get',
    params
  })
}

// 获取用户菜单
export function getUserMenus() {
  return request({
    url: '/api/system/menu/user-menus',
    method: 'get'
  })
}

// 获取操作日志列表
export function getOperLogList(params) {
  return request({
    url: '/api/system/operlog/list',
    method: 'get',
    params
  })
}

// 获取登录日志列表
export function getLogininforList(params) {
  return request({
    url: '/api/system/logininfor/list',
    method: 'get',
    params
  })
}

// 获取字典类型列表
export function getDictTypeList(params) {
  return request({
    url: '/api/system/dict/type/list',
    method: 'get',
    params
  })
}

// 获取字典数据列表
export function getDictDataList(params) {
  return request({
    url: '/api/system/dict/data/list',
    method: 'get',
    params
  })
}