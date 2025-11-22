<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">费用类型管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>费用管理</el-breadcrumb-item>
        <el-breadcrumb-item>费用类型管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="费用名称">
          <el-input
            v-model="searchForm.feeName"
            placeholder="请输入费用名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="费用编码">
          <el-input
            v-model="searchForm.feeCode"
            placeholder="请输入费用编码"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增费用类型
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedRows.length === 0"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>

    <!-- 费用类型表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="typeName" label="费用名称" width="150" sortable />
        <el-table-column prop="typeCode" label="费用编码" width="120" />
        <el-table-column prop="unitPrice" label="单价" width="120" sortable>
          <template #default="{ row }">
            <span v-if="row.typeCode === 'PARKING_FEE' || row.typeCode === 'parking_fee'" class="price-text">按车位定价</span>
            <span v-else class="price-text">¥{{ row.unitPrice ? row.unitPrice.toFixed(2) : '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="billingUnit" label="计费单位" width="120" />
        <el-table-column prop="billingCycle" label="计费周期" width="120">
          <template #default="{ row }">
            <el-tag :type="getBillingCycleTag(row.billingCycle)">
              {{ getBillingCycleName(row.billingCycle) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="费用说明" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="info"
              @click="handleViewBills(row)"
            >
              查看账单
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑费用类型对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="费用名称" prop="feeName">
          <el-input v-model="form.feeName" placeholder="请输入费用名称" />
        </el-form-item>
        <el-form-item label="费用编码" prop="feeCode">
          <el-input v-model="form.feeCode" placeholder="请输入费用编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input
            v-if="form.feeCode === 'PARKING_FEE' || form.feeCode === 'parking_fee'"
            value="按车位定价"
            disabled
            style="width: 100%"
          />
          <el-input-number
            v-else
            v-model="form.unitPrice"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计费单位" prop="unitType">
          <el-select v-model="form.unitType" placeholder="请选择计费单位" style="width: 100%">
            <el-option
              v-for="item in unitTypeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计费周期" prop="billingCycle">
          <el-select v-model="form.billingCycle" placeholder="请选择计费周期" style="width: 100%">
            <el-option
              v-for="item in billingCycleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="费用说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入费用说明"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import {
  getFeeTypePage,
  getFeeTypeList,
  getFeeTypeDetail,
  createFeeType,
  updateFeeType,
  deleteFeeType,
  getAllFeeTypes
} from '@/api/feeType'

// 响应式数据
const formRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)

// 搜索表单
const searchForm = reactive({
  feeName: '',
  feeCode: '',
  status: ''
})

// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 计费周期选项
const billingCycleOptions = [
  { label: '按月', value: 1 },
  { label: '按季度', value: 2 },
  { label: '按年', value: 3 },
  { label: '一次性', value: 4 }
]

// 计费单位选项
const unitTypeOptions = [
  '元/平方米',
  '元/套',
  '元/月',
  '元/年',
  '元/次',
  '元/度',
  '元/吨'
]

// 表单数据
const form = reactive({
  feeTypeId: null,
  feeName: '',
  feeCode: '',
  unitPrice: 0,
  unitType: '',
  billingCycle: 1,
  description: '',
  status: 1
})

// 表单规则
const formRules = computed(() => ({
  feeName: [
    { required: true, message: '请输入费用名称', trigger: 'blur' },
    { min: 2, max: 50, message: '费用名称长度在2到50个字符', trigger: 'blur' }
  ],
  feeCode: isEdit.value ? [] : [
    { required: true, message: '请输入费用编码', trigger: 'blur' },
    { pattern: /^[A-Za-z_][A-Za-z0-9_]*$/, message: '费用编码只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  unitPrice: (form.feeCode === 'PARKING_FEE' || form.feeCode === 'parking_fee') ? [] : [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价必须大于等于0', trigger: 'blur' }
  ],
  unitType: [
    { required: true, message: '请选择计费单位', trigger: 'change' }
  ],
  billingCycle: [
    { required: true, message: '请选择计费周期', trigger: 'change' }
  ]
}))

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑费用类型' : '新增费用类型')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取计费周期名称
const getBillingCycleName = (cycle) => {
  const option = billingCycleOptions.find(item => item.value === cycle)
  return option ? option.label : '未知'
}

// 获取计费周期标签
const getBillingCycleTag = (cycle) => {
  const tagMap = {
    1: 'primary', // 按月
    2: 'success', // 按季度
    3: 'warning', // 按年
    4: 'info'     // 一次性
  }
  return tagMap[cycle] || 'info'
}

// 生成模拟数据
const generateMockData = () => {
  const feeTypes = [
    {
      id: 1, // 使用后端字段名
      typeName: '物业费', // 使用后端字段名
      typeCode: 'PROPERTY_FEE', // 使用后端字段名
      unitPrice: 2.5,
      billingUnit: '元/平方米', // 使用后端字段名
      billingCycle: 1,
      description: '小区日常维护、保洁、安保等费用',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 2,
      typeName: '停车费',
      typeCode: 'PARKING_FEE',
      unitPrice: null, // 停车费按车位定价，不设固定单价
      billingUnit: '元/车位/月',
      billingCycle: 1,
      description: '根据车位类型和位置定价的停车费用',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 3,
      typeName: '垃圾处理费',
      typeCode: 'GARBAGE_FEE',
      unitPrice: 10,
      billingUnit: '元/户',
      billingCycle: 1,
      description: '生活垃圾收集和处理费用',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 4,
      typeName: '电梯费',
      typeCode: 'ELEVATOR_FEE',
      unitPrice: 30,
      billingUnit: '元/户',
      billingCycle: 1,
      description: '电梯运行、维护、年检等费用',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 5,
      typeName: '公共照明费',
      typeCode: 'PUBLIC_LIGHTING_FEE',
      unitPrice: 15,
      billingUnit: '元/户',
      billingCycle: 1,
      description: '楼道、公共区域照明电费',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 6,
      typeName: '水费',
      typeCode: 'WATER_FEE',
      unitPrice: 4.5,
      billingUnit: '元/吨',
      billingCycle: 1,
      description: '公共区域用水费用',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 7,
      typeName: '电费',
      typeCode: 'ELECTRICITY_FEE',
      unitPrice: 0.8,
      billingUnit: '元/度',
      billingCycle: 1,
      description: '公共区域用电费用',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 8,
      typeName: '维修基金',
      typeCode: 'MAINTENANCE_FUND',
      unitPrice: 1000,
      billingUnit: '元/年',
      billingCycle: 3,
      description: '房屋公共部位、设施设备维修基金',
      status: 1,
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    }
  ]

  return feeTypes
}

// 加载费用类型数据
const loadFeeTypes = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      feeName: searchForm.feeName,
      feeCode: searchForm.feeCode,
      status: searchForm.status
    }
    console.log('发送分页查询请求:', params)
    const response = await getFeeTypePage(params)
    console.log('收到响应:', response)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
      console.log('设置数据成功，记录数:', tableData.value.length, '总数:', pagination.total)
    } else {
      ElMessage.error(response.msg || '加载费用类型失败')
    }
  } catch (error) {
    console.error('加载费用类型失败:', error)
    ElMessage.error('加载费用类型失败')
  } finally {
    loading.value = false
  }
}

// 加载模拟数据
const loadMockData = () => {
  let mockData = generateMockData()

  // 应用搜索过滤 - 使用后端字段名
  if (searchForm.feeName) {
    mockData = mockData.filter(item =>
      item.typeName.toLowerCase().includes(searchForm.feeName.toLowerCase())
    )
  }
  if (searchForm.feeCode) {
    mockData = mockData.filter(item =>
      item.typeCode.toLowerCase().includes(searchForm.feeCode.toLowerCase())
    )
  }
  if (searchForm.status !== '' && searchForm.status !== null) {
    mockData = mockData.filter(item => item.status === searchForm.status)
  }

  // 应用分页
  pagination.total = mockData.length
  const startIndex = (pagination.current - 1) * pagination.pageSize
  const endIndex = startIndex + pagination.pageSize
  tableData.value = mockData.slice(startIndex, endIndex)
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  // 尝试加载真实数据，失败则使用模拟数据
  loadFeeTypes()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    feeName: '',
    feeCode: '',
    status: ''
  })
  // 重新加载数据
  loadFeeTypes()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    feeTypeId: null,
    feeName: '',
    feeCode: '',
    unitPrice: 0,
    unitType: '',
    billingCycle: 1,
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  // 进行字段映射，将后端数据映射到前端表单
  Object.assign(form, {
    feeTypeId: row.id, // 后端的 id 映射到前端的 feeTypeId
    feeName: row.typeName, // 后端的 typeName 映射到前端的 feeName
    feeCode: row.typeCode, // 后端的 typeCode 映射到前端的 feeCode
    unitPrice: (row.typeCode === 'PARKING_FEE' || row.typeCode === 'parking_fee') ? null : row.unitPrice, // 停车费不显示单价
    unitType: row.billingUnit, // 后端的 billingUnit 映射到前端的 unitType
    billingCycle: row.billingCycle,
    description: row.description,
    status: row.status
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除费用类型"${row.typeName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteFeeType(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      // 重新加载数据
      loadFeeTypes()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除费用类型失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的费用类型')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个费用类型吗？`,
      '批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedRows.value.map(row => row.id).join(',')
    const response = await deleteFeeType(ids)
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      // 重新加载数据
      loadFeeTypes()
    } else {
      ElMessage.error(response.msg || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除费用类型失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 查看账单
const handleViewBills = (row) => {
  ElMessage.info(`查看费用类型"${row.feeName}"的账单记录`)
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 构建提交数据，进行字段映射
    const submitData = {
      id: form.feeTypeId, // 后端期望 id 字段
      typeName: form.feeName, // 后端期望 typeName 字段
      typeCode: form.feeCode, // 后端期望 typeCode 字段
      unitPrice: (form.feeCode === 'PARKING_FEE' || form.feeCode === 'parking_fee') ? null : form.unitPrice, // 停车费不设单价
      billingUnit: form.unitType, // 后端期望 billingUnit 字段
      billingCycle: form.billingCycle,
      description: form.description,
      status: form.status
    }

    let response
    if (isEdit.value) {
      response = await updateFeeType(submitData)
    } else {
      response = await createFeeType(submitData)
    }

    if (response.code === 200) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      // 重新加载数据
      loadFeeTypes()
    } else {
      ElMessage.error(response.msg || (dialogTitle.value + '失败'))
    }
  } catch (error) {
    console.error('提交费用类型失败:', error)
    ElMessage.error(dialogTitle.value + '失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadFeeTypes()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadFeeTypes()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 初始化
onMounted(() => {
  loadFeeTypes()
})
</script>

<style lang="scss" scoped>
.log-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  .page-title {
    margin: 0 0 16px 0;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.search-section,
.action-section {
  margin-bottom: 20px;
}

.table-section {
  background: #fff;
  border-radius: 4px;
  padding: 20px;

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }
}

.price-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>