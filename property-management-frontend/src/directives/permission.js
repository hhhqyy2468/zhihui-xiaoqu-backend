import { useUserStore } from '@/stores/user'
import { hasPermission, hasRole, hasUserType } from '@/utils/permission'

/**
 * 权限指令 v-permission
 * 用法：
 * v-permission="'system:user:view'"
 * v-permission="{ permission: 'system:user:view', mode: 'any' }"
 * v-permission="['system:user:view', 'system:user:add']"
 */
export const permission = {
  mounted(el, binding) {
    const { value } = binding

    if (value && !hasPermission(value)) {
      // 移除DOM元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  },

  updated(el, binding) {
    const { value, oldValue } = binding

    if (value !== oldValue) {
      if (value && !hasPermission(value)) {
        // 移除DOM元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

/**
 * 角色指令 v-role
 * 用法：
 * v-role="'admin'"
 * v-role="{ role: 'admin', mode: 'any' }"
 * v-role="['admin', 'manager']"
 */
export const role = {
  mounted(el, binding) {
    const { value } = binding

    if (value && !hasRole(value)) {
      // 移除DOM元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  },

  updated(el, binding) {
    const { value, oldValue } = binding

    if (value !== oldValue) {
      if (value && !hasRole(value)) {
        // 移除DOM元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

/**
 * 用户类型指令 v-user-type
 * 用法：
 * v-user-type="1"
 * v-user-type="[1, 2]"
 */
export const userType = {
  mounted(el, binding) {
    const { value } = binding

    if (value && !hasUserType(value)) {
      // 移除DOM元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  },

  updated(el, binding) {
    const { value, oldValue } = binding

    if (value !== oldValue) {
      if (value && !hasUserType(value)) {
        // 移除DOM元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

// 默认导出permission指令（向后兼容）
export default permission
