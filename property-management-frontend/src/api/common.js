import request from '@/utils/request'

/**
 * 通用接口API
 */

// 文件上传
export function uploadFile(file, businessType, businessId) {
  const formData = new FormData()
  formData.append('file', file)
  if (businessType) {
    formData.append('businessType', businessType)
  }
  if (businessId) {
    formData.append('businessId', businessId)
  }

  return request({
    url: '/api/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 批量文件上传
export function uploadFiles(files, businessType, businessId) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  if (businessType) {
    formData.append('businessType', businessType)
  }
  if (businessId) {
    formData.append('businessId', businessId)
  }

  return request({
    url: '/api/common/upload/batch',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取文件信息
export function getFileInfo(fileId) {
  return request({
    url: `/api/common/file/${fileId}`,
    method: 'get'
  })
}

// 删除文件
export function deleteFile(fileId) {
  return request({
    url: `/api/common/file/${fileId}`,
    method: 'delete'
  })
}

// 获取文件下载链接
export function downloadFile(fileId) {
  return request({
    url: `/api/common/file/${fileId}/download`,
    method: 'get'
  })
}

// 获取文件预览URL
export function getFilePreviewUrl(fileId) {
  return request({
    url: `/api/common/file/${fileId}/preview`,
    method: 'get'
  })
}

// 获取字典类型列表
export function getDictTypes() {
  return request({
    url: '/api/common/dict/types',
    method: 'get'
  })
}

// 根据字典类型获取字典数据
export function getDictDataByType(dictType) {
  return request({
    url: `/api/common/dict/data/${dictType}`,
    method: 'get'
  })
}

// 获取系统配置
export function getSystemConfig() {
  return request({
    url: '/api/common/system/config',
    method: 'get'
  })
}

// 更新系统配置
export function updateSystemConfig(data) {
  return request({
    url: '/api/common/system/config',
    method: 'put',
    data
  })
}

// 获取支持的文件类型
export function getSupportedFileTypes() {
  return request({
    url: '/api/common/file-types',
    method: 'get'
  })
}

// 压缩文件
export function compressFile(fileId) {
  return request({
    url: `/api/common/file/${fileId}/compress`,
    method: 'post'
  })
}

// 解压文件
export function extractFile(fileId) {
  return request({
    url: `/api/common/file/${fileId}/extract`,
    method: 'post'
  })
}