import axios from 'axios'
import ErrorHandler from './errorHandler'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_APP_API_BASE_URL || '/api/v1',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加认证token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 添加请求时间戳（防止缓存）
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }

    return config
  },
  error => {
    // 请求错误处理
    return ErrorHandler.handle(error, {
      showMessage: false, // 请求错误不显示消息，由响应错误统一处理
      logError: true
    })
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data

    // 如果是下载文件等二进制数据，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }

    // 正常响应（code === 200）
    if (res.code === 200) {
      return res
    }

    // 业务错误（code !== 200）
    // 构造错误对象，包含响应数据
    const error = new Error(res.msg || '操作失败')
    error.response = {
      ...response,
      data: res
    }
    error.code = res.code

    return ErrorHandler.handle(error, {
      showMessage: true,
      logError: true
    })
  },
  error => {
    // HTTP错误处理
    return ErrorHandler.handle(error, {
      showMessage: true,
      logError: true
    })
  }
)

export default request