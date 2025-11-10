// 前端字段名到后端字段名的映射
const fieldMapping = {
  // 楼栋字段
  buildingNo: 'building_no',
  buildingName: 'building_name',
  floorCount: 'floor_count',
  unitCount: 'unit_count',
  buildYear: 'build_year',
  
  // 单元字段
  unitNo: 'unit_no',
  unitName: 'unit_name',
  roomCountPerFloor: 'room_count_per_floor',
  
  // 房产字段
  houseNo: 'house_no',
  roomNumber: 'room_number',
  floor: 'floor',
  houseType: 'house_type',
  buildingArea: 'building_area',
  usableArea: 'usable_area',
  houseStatus: 'house_status',
  propertyOwner: 'property_owner',
  
  // 通用字段
  createTime: 'create_time',
  updateTime: 'update_time',
  createBy: 'create_by',
  updateBy: 'update_by'
}

// 将前端字段名转换为后端字段名
export function toBackendField(frontendField) {
  return fieldMapping[frontendField] || frontendField
}

// 将后端字段名转换为前端字段名
export function toFrontendField(backendField) {
  const reverseMapping = Object.fromEntries(
    Object.entries(fieldMapping).map(([front, back]) => [back, front])
  )
  return reverseMapping[backendField] || backendField
}

// 转换整个对象（前端 → 后端）
export function convertToBackendFormat(obj) {
  const result = {}
  for (const [key, value] of Object.entries(obj)) {
    const backendKey = toBackendField(key)
    result[backendKey] = value
  }
  return result
}

// 转换整个对象（后端 → 前端）
export function convertToFrontendFormat(obj) {
  const result = {}
  for (const [key, value] of Object.entries(obj)) {
    const frontendKey = toFrontendField(key)
    result[frontendKey] = value
  }
  return result
}

// 转换数组中的对象
export function convertArrayToFrontendFormat(array) {
  return array.map(item => convertToFrontendFormat(item))
}

export function convertArrayToBackendFormat(array) {
  return array.map(item => convertToBackendFormat(item))
}