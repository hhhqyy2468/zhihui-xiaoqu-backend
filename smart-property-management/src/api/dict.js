import request from '@/utils/request'

// 字典类型API

// 获取字典类型分页列表
export function getDictTypePage(params) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params
  })
}

// 获取字典类型详情
export function getDictTypeDetail(dictId) {
  return request({
    url: `/system/dict/type/${dictId}`,
    method: 'get'
  })
}

// 新增字典类型
export function addDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

// 修改字典类型
export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

// 删除字典类型
export function deleteDictType(dictIds) {
  return request({
    url: `/system/dict/type/${dictIds}`,
    method: 'delete'
  })
}

// 刷新字典缓存
export function refreshDictCache() {
  return request({
    url: '/system/dict/type/refreshCache',
    method: 'delete'
  })
}

// 获取字典选择框列表
export function getDictTypeOptions() {
  return request({
    url: '/system/dict/type/optionselect',
    method: 'get'
  })
}

// 根据字典类型查询字典详细信息
export function getDictTypeByType(dictType) {
  return request({
    url: `/system/dict/type/type/${dictType}`,
    method: 'get'
  })
}

// 字典数据API

// 获取字典数据分页列表
export function getDictDataPage(params) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params
  })
}

// 获取字典数据详情
export function getDictDataDetail(dictCode) {
  return request({
    url: `/system/dict/data/${dictCode}`,
    method: 'get'
  })
}

// 根据字典类型查询字典数据信息
export function getDictDataByType(dictType) {
  return request({
    url: `/system/dict/data/type/${dictType}`,
    method: 'get'
  })
}

// 新增字典数据
export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

// 修改字典数据
export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

// 删除字典数据
export function deleteDictData(dictCodes) {
  return request({
    url: `/system/dict/data/${dictCodes}`,
    method: 'delete'
  })
}

// 获取字典数据选择框列表
export function getDictDataOptions(dictType) {
  return request({
    url: '/system/dict/data/optionselect',
    method: 'get',
    params: { dictType }
  })
}

// 查询所有字典数据列表
export function getAllDictData() {
  return request({
    url: '/system/dict/data/all',
    method: 'get'
  })
}

// 导出字典数据
export function exportDictData(params) {
  return request({
    url: '/system/dict/data/export',
    method: 'get',
    params
  })
}

// 批量导入字典数据
export function importDictData(data) {
  return request({
    url: '/system/dict/data/import',
    method: 'post',
    data
  })
}