/**
 * API统一入口
 * 简化API调用，统一错误处理
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import config from '../config'

// 创建axios实例
const api = axios.create({
  baseURL: config.API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('API请求错误:', error)
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// 通用请求方法
const request = {
  get: (url, params) => api.get(url, { params }),
  post: (url, data) => api.post(url, data),
  put: (url, data) => api.put(url, data),
  delete: (url) => api.delete(url)
}

// 导入各模块API
import * as authApi from './auth'
import * as buildingApi from './building'
import * as houseApi from './house'
import * as unitApi from './unit'
import * as feeApi from './feetype'
import * as billApi from './bill'
import * as noticeApi from './notice'
import * as repairApi from './repair'
import * as ownerApi from './owner'
import * as walletApi from './wallet'
import * as workbenchApi from './workbench'
import * as commonApi from './common'
import * as systemApi from './system'

// 导出各模块API
export {
  authApi,
  buildingApi,
  houseApi,
  unitApi,
  feeApi,
  billApi,
  noticeApi,
  repairApi,
  ownerApi,
  walletApi,
  workbenchApi,
  commonApi,
  systemApi
}

export default request