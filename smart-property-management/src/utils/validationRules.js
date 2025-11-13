// 通用验证规则
export const commonRules = {
  required: { required: true, message: '此项为必填项', trigger: 'blur' },
  requiredChange: { required: true, message: '此项为必选项', trigger: 'change' },
  
  // 数字验证
  positiveInteger: { type: 'number', min: 1, message: '必须为正整数', trigger: 'blur' },
  positiveNumber: { type: 'number', min: 0, message: '必须为非负数', trigger: 'blur' },
  
  // 长度验证
  maxLength: (max) => ({ max, message: `长度不能超过${max}个字符`, trigger: 'blur' }),
  minLength: (min) => ({ min, message: `长度不能少于${min}个字符`, trigger: 'blur' }),
  rangeLength: (min, max) => ({ min, max, message: `长度在${min}到${max}个字符之间`, trigger: 'blur' }),
  
  // 数字范围验证
  numberRange: (min, max) => ({ type: 'number', min, max, message: `数值必须在${min}-${max}之间`, trigger: 'blur' })
}

// 楼栋相关验证规则
export const buildingRules = {
  buildingNo: [
    commonRules.required,
    { pattern: /^[A-Z0-9-]+$/, message: '楼栋编号只能包含大写字母、数字和连字符', trigger: 'blur' },
    commonRules.rangeLength(1, 50)
  ],
  buildingName: [
    commonRules.required,
    commonRules.rangeLength(2, 100)
  ],
  floorCount: [
    commonRules.required,
    commonRules.numberRange(1, 999),
    { pattern: /^[1-9]\d*$/, message: '楼层数必须为正整数', trigger: 'blur' }
  ],
  unitCount: [
    commonRules.required,
    commonRules.numberRange(1, 99),
    { pattern: /^[1-9]\d*$/, message: '单元数必须为正整数', trigger: 'blur' }
  ],
  address: [
    commonRules.required,
    commonRules.rangeLength(5, 255)
  ],
  buildYear: [
    commonRules.required,
    commonRules.numberRange(1900, new Date().getFullYear()),
    { pattern: /^(19|20)\d{2}$/, message: '请输入有效的年份格式', trigger: 'blur' }
  ],
  remark: [
    commonRules.maxLength(500)
  ]
}

// 单元相关验证规则
export const unitRules = {
  unitNo: [
    commonRules.required,
    { pattern: /^[A-Z0-9-]+$/, message: '单元编号只能包含大写字母、数字和连字符', trigger: 'blur' },
    commonRules.rangeLength(1, 50)
  ],
  unitName: [
    commonRules.required,
    commonRules.rangeLength(2, 100)
  ],
  floorCount: [
    commonRules.required,
    commonRules.numberRange(1, 999),
    { pattern: /^[1-9]\d*$/, message: '楼层数必须为正整数', trigger: 'blur' }
  ],
  roomCountPerFloor: [
    commonRules.required,
    commonRules.numberRange(1, 99),
    { pattern: /^[1-9]\d*$/, message: '每层房间数必须为正整数', trigger: 'blur' }
  ],
  buildingId: [
    commonRules.requiredChange
  ]
}

// 房产相关验证规则
export const houseRules = {
  houseNo: [
    commonRules.required,
    commonRules.rangeLength(1, 50)
  ],
  roomNumber: [
    commonRules.required,
    commonRules.rangeLength(1, 50)
  ],
  floor: [
    commonRules.required,
    commonRules.numberRange(1, 999),
    { pattern: /^[1-9]\d*$/, message: '楼层必须为正整数', trigger: 'blur' }
  ],
  houseType: [
    commonRules.requiredChange
  ],
  buildingArea: [
    commonRules.required,
    commonRules.numberRange(1, 999999.99),
    { pattern: /^\d+(\.\d{1,2})?$/, message: '建筑面积最多保留两位小数', trigger: 'blur' }
  ],
  usableArea: [
    commonRules.required,
    commonRules.numberRange(1, 999999.99),
    { pattern: /^\d+(\.\d{1,2})?$/, message: '使用面积最多保留两位小数', trigger: 'blur' }
  ],
  houseStatus: [
    commonRules.requiredChange
  ],
  buildingId: [
    commonRules.requiredChange
  ],
  unitId: [
    commonRules.requiredChange
  ]
}

// 面积验证规则
export const areaRules = {
  buildingArea: [
    commonRules.required,
    commonRules.numberRange(1, 999999.99),
    { pattern: /^\d+(\.\d{1,2})?$/, message: '建筑面积最多保留两位小数', trigger: 'blur' }
  ],
  usableArea: [
    commonRules.required,
    commonRules.numberRange(1, 999999.99),
    { pattern: /^\d+(\.\d{1,2})?$/, message: '使用面积最多保留两位小数', trigger: 'blur' }
  ]
}

// 金额验证规则
export const amountRules = {
  amount: [
    commonRules.required,
    commonRules.numberRange(0.01, 99999999.99),
    { pattern: /^\d+(\.\d{1,2})?$/, message: '金额最多保留两位小数', trigger: 'blur' }
  ]
}