/**
 * API响应统一处理工具
 */

/**
 * 统一的成功响应处理
 * @param {Object} response - API响应对象
 * @param {Function} successCallback - 成功回调函数
 * @param {Function} errorCallback - 错误回调函数
 */
export function handleResponse(response, successCallback, errorCallback) {
  if (response.code === 200 || response.code === '200') {
    if (successCallback) {
      successCallback(response.data)
    }
    return response.data
  } else {
    if (errorCallback) {
      errorCallback(response.message || '操作失败')
    } else {
      console.error('API Error:', response.message)
    }
    throw new Error(response.message || '操作失败')
  }
}

/**
 * 统一的错误处理
 * @param {Error} error - 错误对象
 * @param {String} defaultMessage - 默认错误消息
 */
export function handleError(error, defaultMessage = '操作失败') {
  console.error('API Error:', error)

  if (error.response) {
    // 服务器返回了错误状态码
    const { status, data } = error.response

    switch (status) {
      case 400:
        throw new Error(data.message || '请求参数错误')
      case 401:
        throw new Error('登录已过期，请重新登录')
      case 403:
        throw new Error('没有权限访问该资源')
      case 404:
        throw new Error('请求的资源不存在')
      case 500:
        throw new Error('服务器内部错误')
      default:
        throw new Error(data.message || defaultMessage)
    }
  } else if (error.request) {
    // 请求已发送，但没有收到响应
    throw new Error('网络连接失败，请检查网络设置')
  } else {
    // 其他错误
    throw new Error(error.message || defaultMessage)
  }
}

/**
 * 处理分页响应
 * @param {Object} response - API响应对象
 * @returns {Object} 分页数据
 */
export function handlePageResponse(response) {
  if (response.code === 200 || response.code === '200') {
    const data = response.data
    return {
      list: data.list || data.records || data,
      total: data.total || data.totalCount || 0,
      pageNum: data.pageNum || data.pageNumber || 1,
      pageSize: data.pageSize || data.size || 10
    }
  }
  throw new Error(response.message || '获取数据失败')
}

/**
 * 处理文件下载响应
 * @param {Object} response - API响应对象
 * @param {String} fileName - 文件名
 */
export function handleFileDownload(response, fileName) {
  if (response.code === 200 || response.code === '200') {
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName || `文件_${Date.now()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } else {
    throw new Error(response.message || '文件下载失败')
  }
}

/**
 * 格式化API响应数据
 * @param {Object} data - 原始数据
 * @returns {Object} 格式化后的数据
 */
export function formatResponseData(data) {
  if (!data) return null

  // 处理时间格式
  if (data.createTime || data.updateTime) {
    return {
      ...data,
      createTime: data.createTime ? new Date(data.createTime).toLocaleString() : null,
      updateTime: data.updateTime ? new Date(data.updateTime).toLocaleString() : null
    }
  }

  return data
}

/**
 * 批量处理响应数据
 * @param {Array} list - 数据列表
 * @returns {Array} 格式化后的数据列表
 */
export function formatResponseList(list) {
  if (!Array.isArray(list)) return []
  return list.map(item => formatResponseData(item))
}

/**
 * API请求拦截器配置
 */
export const apiConfig = {
  timeout: 10000, // 请求超时时间
  baseURL: import.meta.env.VITE_BASE_API || '/api',
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
}

/**
 * 常用API状态码
 */
export const API_STATUS = {
  SUCCESS: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
}

/**
 * 常用错误消息
 */
export const ERROR_MESSAGES = {
  NETWORK_ERROR: '网络连接失败，请检查网络设置',
  TIMEOUT_ERROR: '请求超时，请稍后重试',
  AUTH_ERROR: '登录已过期，请重新登录',
  PERMISSION_ERROR: '没有权限访问该资源',
  NOT_FOUND_ERROR: '请求的资源不存在',
  SERVER_ERROR: '服务器内部错误，请稍后重试',
  UNKNOWN_ERROR: '未知错误，请稍后重试'
}