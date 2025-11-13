// 计算楼栋总户数
export function calculateTotalHouseholds(building) {
  if (!building) return 0
  
  // 假设每层每单元有3户
  const householdsPerUnit = 3
  return (building.floorCount || 0) * (building.unitCount || 0) * householdsPerUnit
}

// 计算单元总房间数
export function calculateTotalRooms(unit) {
  if (!unit) return 0
  return (unit.floorCount || 0) * (unit.roomCountPerFloor || 0)
}

// 计算入住率
export function calculateOccupancyRate(totalRooms, occupiedRooms) {
  if (!totalRooms || totalRooms === 0) return 0
  return Math.round((occupiedRooms / totalRooms) * 100)
}

// 格式化面积显示
export function formatArea(area) {
  if (!area && area !== 0) return '0'
  return typeof area === 'number' ? area.toFixed(2) : parseFloat(area).toFixed(2)
}

// 格式化金额显示
export function formatAmount(amount) {
  if (!amount && amount !== 0) return '0.00'
  const num = typeof amount === 'number' ? amount : parseFloat(amount)
  return num.toFixed(2)
}

// 计算楼栋统计信息
export function calculateBuildingStats(buildings) {
  const stats = {
    totalBuildings: buildings.length,
    totalHouseholds: 0,
    totalUnits: 0,
    averageFloors: 0,
    averageUnits: 0
  }
  
  if (buildings.length === 0) return stats
  
  let totalFloors = 0
  let totalUnitCount = 0
  
  buildings.forEach(building => {
    stats.totalHouseholds += calculateTotalHouseholds(building)
    stats.totalUnits += building.unitCount || 0
    totalFloors += building.floorCount || 0
    totalUnitCount += building.unitCount || 0
  })
  
  stats.averageFloors = Math.round(totalFloors / buildings.length)
  stats.averageUnits = Math.round(totalUnitCount / buildings.length)
  
  return stats
}

// 计算单元统计信息
export function calculateUnitStats(units) {
  const stats = {
    totalUnits: units.length,
    totalRooms: 0,
    averageFloors: 0,
    averageRoomsPerFloor: 0
  }
  
  if (units.length === 0) return stats
  
  let totalFloors = 0
  let totalRoomsPerFloor = 0
  
  units.forEach(unit => {
    stats.totalRooms += calculateTotalRooms(unit)
    totalFloors += unit.floorCount || 0
    totalRoomsPerFloor += unit.roomCountPerFloor || 0
  })
  
  stats.averageFloors = Math.round(totalFloors / units.length)
  stats.averageRoomsPerFloor = Math.round(totalRoomsPerFloor / units.length)
  
  return stats
}