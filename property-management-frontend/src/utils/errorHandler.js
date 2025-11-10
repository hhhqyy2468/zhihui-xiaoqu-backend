/**
 * 统一错误处理工具类
 * 提供统一的错误处理、错误码映射、错误消息格式化等功能
 */

import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * HTTP状态码映射
 */
const HTTP_STATUS_MAP = {
  400: '请求参数错误',
  401: '未授权，请重新登录',
  403: '拒绝访问，权限不足',
  404: '请求的资源不存在',
  405: '请求方法不允许',
  408: '请求超时',
  409: '资源冲突',
  422: '请求参数验证失败',
  429: '请求过于频繁，请稍后再试',
  500: '服务器内部错误',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时'
}

/**
 * 业务错误码映射
 */
const BUSINESS_ERROR_MAP = {
  // 认证相关 1000-1099
  1001: '用户名或密码错误',
  1002: '账号已被禁用',
  1003: '账号已过期',
  1004: 'Token已过期',
  1005: 'Token无效',
  1006: '未登录或登录已过期',
  
  // 权限相关 1100-1199
  1101: '无权限访问',
  1102: '无权限执行此操作',
  1103: '角色权限不足',
  
  // 参数验证相关 1200-1299
  1201: '请求参数缺失',
  1202: '请求参数格式错误',
  1203: '请求参数值无效',
  1204: '必填字段未填写',
  
  // 数据相关 1300-1399
  1301: '数据不存在',
  1302: '数据已存在',
  1303: '数据已被使用，无法删除',
  1304: '数据状态不允许此操作',
  1305: '数据关联关系错误',
  
  // 业务逻辑相关 1400-1499
  1401: '操作失败',
  1402: '操作不允许',
  1403: '业务规则校验失败',
  1404: '数据完整性校验失败',
  
  // 系统相关 1500-1599
  1501: '系统异常',
  1502: '数据库操作失败',
  1503: '文件上传失败',
  1504: '文件下载失败',
  1505: '外部服务调用失败'
}

/**
 * 错误类型枚举
 */
export const ErrorType = {
  NETWORK: 'network',           // 网络错误
  HTTP: 'http',                 // HTTP错误
  BUSINESS: 'business',          // 业务错误
  VALIDATION: 'validation',     // 验证错误
  UNKNOWN: 'unknown'            // 未知错误
}

/**
 * 错误处理类
 */
class ErrorHandler {
  /**
   * 处理错误
   * @param {Error} error - 错误对象
   * @param {Object} options - 处理选项
   * @param {boolean} options.showMessage - 是否显示错误消息，默认true
   * @param {boolean} options.logError - 是否记录错误日志，默认true
   * @param {Function} options.onError - 自定义错误处理回调
   * @returns {Promise} 返回Promise.reject
   */
  static handle(error, options = {}) {
    const {
      showMessage = true,
      logError = true,
      onError = null
    } = options

    // 自定义错误处理
    if (onError && typeof onError === 'function') {
      try {
        onError(error)
      } catch (e) {
        console.error('自定义错误处理函数执行失败:', e)
      }
    }

    // 记录错误日志
    if (logError) {
      this.logError(error)
    }

    // 解析错误
    const errorInfo = this.parseError(error)

    // 显示错误消息
    if (showMessage) {
      this.showErrorMessage(errorInfo)
    }

    // 特殊错误处理
    this.handleSpecialError(errorInfo)

    return Promise.reject(error)
  }

  /**
   * 解析错误
   * @param {Error} error - 错误对象
   * @returns {Object} 错误信息对象
   */
  static parseError(error) {
    const errorInfo = {
      type: ErrorType.UNKNOWN,
      code: 0,
      message: '未知错误',
      originalError: error
    }

    // 网络错误
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        errorInfo.type = ErrorType.NETWORK
        errorInfo.code = 408
        errorInfo.message = '请求超时，请检查网络连接'
      } else if (error.message && error.message.includes('Network Error')) {
        errorInfo.type = ErrorType.NETWORK
        errorInfo.code = 0
        errorInfo.message = '网络连接异常，请检查网络设置'
      } else {
        errorInfo.type = ErrorType.NETWORK
        errorInfo.message = error.message || '网络错误'
      }
      return errorInfo
    }

    // HTTP错误
    const response = error.response
    const status = response.status
    const data = response.data || {}

    errorInfo.type = ErrorType.HTTP
    errorInfo.code = status
    errorInfo.message = HTTP_STATUS_MAP[status] || `HTTP错误 ${status}`

    // 业务错误（有业务错误码）
    if (data.code && data.code !== 200) {
      errorInfo.type = ErrorType.BUSINESS
      errorInfo.code = data.code
      errorInfo.message = data.msg || BUSINESS_ERROR_MAP[data.code] || '操作失败'
      
      // 验证错误（422状态码或特定错误码）
      if (status === 422 || (data.code >= 1200 && data.code < 1300)) {
        errorInfo.type = ErrorType.VALIDATION
        errorInfo.validationErrors = data.data || {}
      }
    }

    return errorInfo
  }

  /**
   * 显示错误消息
   * @param {Object} errorInfo - 错误信息对象
   */
  static showErrorMessage(errorInfo) {
    const { type, code, message, validationErrors } = errorInfo

    // 验证错误显示详细信息
    if (type === ErrorType.VALIDATION && validationErrors) {
      const errorMessages = Object.values(validationErrors).flat()
      if (errorMessages.length > 0) {
        ElMessage({
          message: errorMessages.join('；'),
          type: 'error',
          duration: 5000,
          showClose: true
        })
        return
      }
    }

    // 普通错误消息
    ElMessage({
      message: message,
      type: 'error',
      duration: 5000,
      showClose: true
    })
  }

  /**
   * 处理特殊错误
   * @param {Object} errorInfo - 错误信息对象
   */
  static handleSpecialError(errorInfo) {
    const { code, type } = errorInfo

    // 401未授权 - 跳转登录页
    if (code === 401 || (type === ErrorType.BUSINESS && code === 1006)) {
      this.handleUnauthorized()
      return
    }

    // 403无权限 - 显示权限提示
    if (code === 403 || (type === ErrorType.BUSINESS && code >= 1100 && code < 1200)) {
      // 可以在这里添加权限不足的特殊处理
      return
    }

    // 500服务器错误 - 记录详细日志
    if (code === 500) {
      console.error('服务器内部错误，请联系管理员', errorInfo.originalError)
    }
  }

  /**
   * 处理未授权错误
   */
  static handleUnauthorized() {
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
    
    // 显示提示
    ElMessageBox.alert('登录已过期，请重新登录', '提示', {
      confirmButtonText: '确定',
      type: 'warning',
      callback: () => {
        // 跳转到登录页
        window.location.href = '/login'
      }
    })
  }

  /**
   * 记录错误日志
   * @param {Error} error - 错误对象
   */
  static logError(error) {
    // 开发环境输出详细错误信息
    if (import.meta.env.DEV) {
      console.error('错误详情:', error)
      if (error.response) {
        console.error('响应数据:', error.response.data)
        console.error('请求配置:', error.config)
      }
    }

    // 生产环境可以发送错误日志到服务器
    if (import.meta.env.PROD) {
      // TODO: 发送错误日志到日志服务器
      // this.sendErrorLog(error)
    }
  }

  /**
   * 发送错误日志到服务器（可选）
   * @param {Error} error - 错误对象
   */
  static sendErrorLog(error) {
    // 实现错误日志上报逻辑
    // 例如：发送到日志收集服务
  }

  /**
   * 格式化验证错误
   * @param {Object} validationErrors - 验证错误对象
   * @returns {string} 格式化后的错误消息
   */
  static formatValidationErrors(validationErrors) {
    if (!validationErrors || typeof validationErrors !== 'object') {
      return '参数验证失败'
    }

    const messages = []
    for (const [field, errors] of Object.entries(validationErrors)) {
      if (Array.isArray(errors)) {
        messages.push(...errors)
      } else if (typeof errors === 'string') {
        messages.push(errors)
      }
    }

    return messages.length > 0 ? messages.join('；') : '参数验证失败'
  }

  /**
   * 创建业务错误
   * @param {number} code - 错误码
   * @param {string} message - 错误消息
   * @returns {Error} 错误对象
   */
  static createBusinessError(code, message) {
    const error = new Error(message)
    error.code = code
    error.type = ErrorType.BUSINESS
    return error
  }

  /**
   * 创建验证错误
   * @param {Object} validationErrors - 验证错误对象
   * @returns {Error} 错误对象
   */
  static createValidationError(validationErrors) {
    const error = new Error(this.formatValidationErrors(validationErrors))
    error.code = 422
    error.type = ErrorType.VALIDATION
    error.validationErrors = validationErrors
    return error
  }
}

export default ErrorHandler

