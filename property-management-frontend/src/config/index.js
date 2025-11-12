/**
 * 应用配置
 */

// 环境配置
const isProduction = import.meta.env.PROD

export const config = {
  // API配置
  API_BASE_URL: import.meta.env.VITE_APP_API_BASE_URL || (isProduction ? '/api' : 'http://localhost:8080/api/v1'),

  // 登录模式：'mock' | 'real' | 'auto'
  // mock: 强制使用模拟登录
  // real: 强制使用真实API登录
  // auto: 先尝试真实API，失败后使用模拟登录（推荐开发环境使用）
  LOGIN_MODE: import.meta.env.VITE_LOGIN_MODE || (isProduction ? 'real' : 'auto'),

  // 应用标题
  APP_TITLE: import.meta.env.VITE_APP_TITLE || '物业管理系统',

  // 上传地址
  UPLOAD_URL: import.meta.env.VITE_APP_UPLOAD_URL || '/api/upload',

  // 是否启用调试日志
  DEBUG: import.meta.env.VITE_APP_ENV !== 'production'
}

export default config