import { useUserStore } from '@/stores/user'

/**
 * 权限检查工具函数
 */

/**
 * 检查是否有指定权限
 * @param {string|Array|Object} permission - 权限值
 * @returns {boolean} 是否有权限
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  const permissions = userStore.permissions

  if (!permission) return true

  if (typeof permission === 'string') {
    return permissions.includes(permission)
  }

  if (Array.isArray(permission)) {
    return permission.some(p => permissions.includes(p))
  }

  if (typeof permission === 'object') {
    const { permission: perm, mode = 'any' } = permission
    const permList = Array.isArray(perm) ? perm : [perm]

    if (mode === 'all') {
      return permList.every(p => permissions.includes(p))
    }
    return permList.some(p => permissions.includes(p))
  }

  return false
}

/**
 * 检查是否有指定角色
 * @param {string|Array|Object} role - 角色值
 * @returns {boolean} 是否有角色
 */
export function hasRole(role) {
  const userStore = useUserStore()
  const roles = userStore.roles

  if (!role) return true

  if (typeof role === 'string') {
    return roles.includes(role)
  }

  if (Array.isArray(role)) {
    return role.some(r => roles.includes(r))
  }

  if (typeof role === 'object') {
    const { role: r, mode = 'any' } = role
    const roleList = Array.isArray(r) ? r : [r]

    if (mode === 'all') {
      return roleList.every(r => roles.includes(r))
    }
    return roleList.some(r => roles.includes(r))
  }

  return false
}

/**
 * 检查是否为指定用户类型
 * @param {number|Array} userType - 用户类型
 * @returns {boolean} 是否为指定用户类型
 */
export function hasUserType(userType) {
  const userStore = useUserStore()
  const currentUserType = userStore.userType

  if (!userType) return true

  if (typeof userType === 'number') {
    return currentUserType === userType
  }

  if (Array.isArray(userType)) {
    return userType.includes(currentUserType)
  }

  return false
}

/**
 * 检查是否为管理员
 * @returns {boolean} 是否为管理员
 */
export function isAdmin() {
  return hasRole('admin') || hasUserType(1)
}

/**
 * 检查是否为物业管理员
 * @returns {boolean} 是否为物业管理员
 */
export function isPropertyManager() {
  return hasRole('property_manager') || hasUserType(2)
}

/**
 * 检查是否为业主
 * @returns {boolean} 是否为业主
 */
export function isOwner() {
  return hasRole('owner') || hasUserType(3)
}

/**
 * 检查是否为维修人员
 * @returns {boolean} 是否为维修人员
 */
export function isWorker() {
  return hasRole('worker') || hasUserType(4)
}

/**
 * 检查是否可以管理系统模块
 * @returns {boolean} 是否可以管理系统模块
 */
export function canManageSystem() {
  return isAdmin() || isPropertyManager()
}

/**
 * 检查是否可以管理物业模块
 * @returns {boolean} 是否可以管理物业模块
 */
export function canManageProperty() {
  return isAdmin() || isPropertyManager()
}

/**
 * 检查是否可以访问业主门户
 * @returns {boolean} 是否可以访问业主门户
 */
export function canAccessPortal() {
  return isOwner()
}

/**
 * 检查是否可以处理维修工单
 * @returns {boolean} 是否可以处理维修工单
 */
export function canHandleRepair() {
  return isAdmin() || isPropertyManager() || isWorker()
}

/**
 * 检查是否可以处理投诉
 * @returns {boolean} 是否可以处理投诉
 */
export function canHandleComplaint() {
  return isAdmin() || isPropertyManager()
}

/**
 * 权限常量
 */
export const PERMISSIONS = {
  // 系统管理
  SYSTEM_USER_VIEW: 'system:user:view',
  SYSTEM_USER_ADD: 'system:user:add',
  SYSTEM_USER_EDIT: 'system:user:edit',
  SYSTEM_USER_DELETE: 'system:user:delete',
  SYSTEM_USER_RESET_PASSWORD: 'system:user:resetPassword',

  SYSTEM_ROLE_VIEW: 'system:role:view',
  SYSTEM_ROLE_ADD: 'system:role:add',
  SYSTEM_ROLE_EDIT: 'system:role:edit',
  SYSTEM_ROLE_DELETE: 'system:role:delete',

  SYSTEM_MENU_VIEW: 'system:menu:view',
  SYSTEM_MENU_ADD: 'system:menu:add',
  SYSTEM_MENU_EDIT: 'system:menu:edit',
  SYSTEM_MENU_DELETE: 'system:menu:delete',

  SYSTEM_CONFIG_VIEW: 'system:config:view',
  SYSTEM_CONFIG_EDIT: 'system:config:edit',
  SYSTEM_CONFIG_BACKUP: 'system:config:backup',
  SYSTEM_CONFIG_RESTORE: 'system:config:restore',

  // 字典管理
  SYSTEM_DICT_VIEW: 'system:dict:view',
  SYSTEM_DICT_ADD: 'system:dict:add',
  SYSTEM_DICT_EDIT: 'system:dict:edit',
  SYSTEM_DICT_DELETE: 'system:dict:delete',
  SYSTEM_DICT_REFRESH: 'system:dict:refresh',

  // 系统日志
  SYSTEM_LOG_VIEW: 'system:log:view',
  SYSTEM_LOG_EXPORT: 'system:log:export',
  SYSTEM_LOG_CLEAR: 'system:log:clear',

  // 物业管理
  PROPERTY_BUILDING_VIEW: 'property:building:view',
  PROPERTY_BUILDING_ADD: 'property:building:add',
  PROPERTY_BUILDING_EDIT: 'property:building:edit',
  PROPERTY_BUILDING_DELETE: 'property:building:delete',

  PROPERTY_UNIT_VIEW: 'property:unit:view',
  PROPERTY_UNIT_ADD: 'property:unit:add',
  PROPERTY_UNIT_EDIT: 'property:unit:edit',
  PROPERTY_UNIT_DELETE: 'property:unit:delete',

  PROPERTY_HOUSE_VIEW: 'property:house:view',
  PROPERTY_HOUSE_ADD: 'property:house:add',
  PROPERTY_HOUSE_EDIT: 'property:house:edit',
  PROPERTY_HOUSE_DELETE: 'property:house:delete',

  PROPERTY_RESIDENT_VIEW: 'property:resident:view',
  PROPERTY_RESIDENT_ADD: 'property:resident:add',
  PROPERTY_RESIDENT_EDIT: 'property:resident:edit',
  PROPERTY_RESIDENT_DELETE: 'property:resident:delete',

  PROPERTY_OWNER_VIEW: 'property:owner:view',
  PROPERTY_OWNER_ADD: 'property:owner:add',
  PROPERTY_OWNER_EDIT: 'property:owner:edit',
  PROPERTY_OWNER_DELETE: 'property:owner:delete',
  PROPERTY_OWNER_IMPORT: 'property:owner:import',
  PROPERTY_OWNER_EXPORT: 'property:owner:export',

  PROPERTY_FEE_TYPE_VIEW: 'property:feeType:view',
  PROPERTY_FEE_TYPE_ADD: 'property:feeType:add',
  PROPERTY_FEE_TYPE_EDIT: 'property:feeType:edit',
  PROPERTY_FEE_TYPE_DELETE: 'property:feeType:delete',

  PROPERTY_BILL_VIEW: 'property:bill:view',
  PROPERTY_BILL_ADD: 'property:bill:add',
  PROPERTY_BILL_EDIT: 'property:bill:edit',
  PROPERTY_BILL_DELETE: 'property:bill:delete',
  PROPERTY_BILL_GENERATE: 'property:bill:generate',
  PROPERTY_BILL_PAY: 'property:bill:pay',

  PROPERTY_WALLET_VIEW: 'property:wallet:view',
  PROPERTY_WALLET_RECHARGE: 'property:wallet:recharge',
  PROPERTY_WALLET_FREEZE: 'property:wallet:freeze',
  PROPERTY_WALLET_RESET_PASSWORD: 'property:wallet:resetPassword',

  PROPERTY_COMPLAINT_VIEW: 'property:complaint:view',
  PROPERTY_COMPLAINT_ADD: 'property:complaint:add',
  PROPERTY_COMPLAINT_EDIT: 'property:complaint:edit',
  PROPERTY_COMPLAINT_DELETE: 'property:complaint:delete',
  PROPERTY_COMPLAINT_ASSIGN: 'property:complaint:assign',
  PROPERTY_COMPLAINT_HANDLE: 'property:complaint:handle',
  PROPERTY_COMPLAINT_RATE: 'property:complaint:rate',

  PROPERTY_REPAIR_VIEW: 'property:repair:view',
  PROPERTY_REPAIR_ADD: 'property:repair:add',
  PROPERTY_REPAIR_EDIT: 'property:repair:edit',
  PROPERTY_REPAIR_DELETE: 'property:repair:delete',
  PROPERTY_REPAIR_ASSIGN: 'property:repair:assign',
  PROPERTY_REPAIR_HANDLE: 'property:repair:handle',
  PROPERTY_REPAIR_ACCEPT: 'property:repair:accept',

  PROPERTY_PARKING_VIEW: 'property:parking:view',
  PROPERTY_PARKING_ADD: 'property:parking:add',
  PROPERTY_PARKING_EDIT: 'property:parking:edit',
  PROPERTY_PARKING_DELETE: 'property:parking:delete',
  PROPERTY_PARKING_RENT: 'property:parking:rent',
  PROPERTY_PARKING_AUDIT: 'property:parking:audit',
  PROPERTY_PARKING_RENEW: 'property:parking:renew',
  PROPERTY_PARKING_RETURN: 'property:parking:return',

  PROPERTY_NOTICE_VIEW: 'property:notice:view',
  PROPERTY_NOTICE_ADD: 'property:notice:add',
  PROPERTY_NOTICE_EDIT: 'property:notice:edit',
  PROPERTY_NOTICE_DELETE: 'property:notice:delete',
  PROPERTY_NOTICE_PUBLISH: 'property:notice:publish',
  PROPERTY_NOTICE_WITHDRAW: 'property:notice:withdraw',

  // 数据分析
  ANALYTICS_DASHBOARD_VIEW: 'analytics:dashboard:view',
  ANALYTICS_DASHBOARD_EXPORT: 'analytics:dashboard:export',
  ANALYTICS_REPORT_VIEW: 'analytics:report:view',
  ANALYTICS_REPORT_GENERATE: 'analytics:report:generate',
  ANALYTICS_REPORT_DOWNLOAD: 'analytics:report:download',
  ANALYTICS_REPORT_SHARE: 'analytics:report:share',
  ANALYTICS_REPORT_DELETE: 'analytics:report:delete',
  ANALYTICS_REPORT_TEMPLATE: 'analytics:report:template',
  ANALYTICS_REPORT_SCHEDULE: 'analytics:report:schedule',

  // 业主门户
  PORTAL_VIEW: 'portal:view',
  PORTAL_DASHBOARD_VIEW: 'portal:dashboard:view',
  PORTAL_BILL_VIEW: 'portal:bill:view',
  PORTAL_BILL_PAY: 'portal:bill:pay',
  PORTAL_SERVICE_APPLY: 'portal:service:apply',
  PORTAL_PROFILE_VIEW: 'portal:profile:view',
  PORTAL_PROFILE_EDIT: 'portal:profile:edit',

  // 消息通知
  NOTIFICATION_CENTER_VIEW: 'notification:center:view',
  NOTIFICATION_CENTER_SEND: 'notification:center:send',
  NOTIFICATION_CENTER_DELETE: 'notification:center:delete',
  NOTIFICATION_CENTER_BATCH: 'notification:center:batch',
  NOTIFICATION_TEMPLATE_VIEW: 'notification:template:view',
  NOTIFICATION_TEMPLATE_ADD: 'notification:template:add',
  NOTIFICATION_TEMPLATE_EDIT: 'notification:template:edit',
  NOTIFICATION_TEMPLATE_DELETE: 'notification:template:delete',
  NOTIFICATION_SETTINGS_VIEW: 'notification:settings:view',
  NOTIFICATION_SETTINGS_EDIT: 'notification:settings:edit'
}

/**
 * 用户类型常量
 */
export const USER_TYPES = {
  ADMIN: 1, // 系统管理员
  MANAGER: 2, // 物业管理员
  OWNER: 3, // 业主
  WORKER: 4 // 维修人员
}

/**
 * 角色常量
 */
export const ROLES = {
  ADMIN: 'admin',
  PROPERTY_MANAGER: 'property_manager',
  OWNER: 'owner',
  WORKER: 'worker'
}