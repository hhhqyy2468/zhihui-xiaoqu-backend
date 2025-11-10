<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
        class="search-form"
      >
        <el-form-item label="费用名称" prop="feeName">
          <el-input
            v-model="searchForm.feeName"
            placeholder="请输入费用名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="费用编码" prop="feeCode">
          <el-input
            v-model="searchForm.feeCode"
            placeholder="请输入费用编码"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
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
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>费用类型列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:feeType:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增费用类型
            </el-button>
            <el-button
              type="danger"
              v-permission="'property:feeType:delete'"
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
        </div>
      </template>

      <Table
        ref="tableRef"
        :data="tableData"
        :columns="tableColumns"
        :loading="loading"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @page-change="handlePageChange"
        @sort-change="handleSortChange"
      >
        <!-- 单价列 -->
        <template #unitPrice="{ row }">
          <span class="price-text">¥{{ row.unitPrice.toFixed(2) }}</span>
        </template>

        <!-- 计费周期列 -->
        <template #billingCycle="{ row }">
          <el-tag :type="getBillingCycleTag(row.billingCycle)">
            {{ getBillingCycleName(row.billingCycle) }}
          </el-tag>
        </template>

        <!-- 状态列 -->
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'property:feeType:edit'"
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
            v-permission="'property:feeType:delete'"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </Table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <Form
        ref="formRef"
        :model="form"
        :rules="formRules"
        :items="formItems"
        label-width="100px"
      />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import Table from '@/components/Table/index.vue'
import Form from '@/components/Form/index.vue'

// 响应式数据
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
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

// 表格列配置
const tableColumns = [
  {
    type: 'selection',
    width: '55'
  },
  {
    prop: 'feeName',
    label: '费用名称',
    width: '150',
    sortable: true
  },
  {
    prop: 'feeCode',
    label: '费用编码',
    width: '120'
  },
  {
    prop: 'unitPrice',
    label: '单价',
    width: '120',
    slot: 'unitPrice',
    sortable: true
  },
  {
    prop: 'unitType',
    label: '计费单位',
    width: '120'
  },
  {
    prop: 'billingCycle',
    label: '计费周期',
    width: '120',
    slot: 'billingCycle'
  },
  {
    prop: 'description',
    label: '费用说明',
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    label: '状态',
    width: '100',
    slot: 'status'
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: '180',
    sortable: true
  },
  {
    prop: 'operation',
    label: '操作',
    width: '240',
    slot: 'operation',
    fixed: 'right'
  }
]

// 计费周期选项
const billingCycleOptions = [
  { label: '按月', value: 1 },
  { label: '按季度', value: 2 },
  { label: '按年', value: 3 },
  { label: '一次性', value: 4 }
]

// 计费单位选项
const unitTypeOptions = [
  { label: '元/平方米', value: '元/平方米' },
  { label: '元/套', value: '元/套' },
  { label: '元/月', value: '元/月' },
  { label: '元/年', value: '元/年' },
  { label: '元/次', value: '元/次' },
  { label: '元/度', value: '元/度' },
  { label: '元/吨', value: '元/吨' }
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
const formRules = {
  feeName: [
    { required: true, message: '请输入费用名称', trigger: 'blur' },
    { min: 2, max: 50, message: '费用名称长度在2到50个字符', trigger: 'blur' }
  ],
  feeCode: [
    { required: true, message: '请输入费用编码', trigger: 'blur' },
    { pattern: /^[A-Z_][A-Z0-9_]*$/, message: '费用编码只能包含大写字母、数字和下划线', trigger: 'blur' }
  ],
  unitPrice: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价必须大于等于0', trigger: 'blur' }
  ],
  unitType: [
    { required: true, message: '请选择计费单位', trigger: 'change' }
  ],
  billingCycle: [
    { required: true, message: '请选择计费周期', trigger: 'change' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'feeName',
    label: '费用名称',
    type: 'input',
    placeholder: '请输入费用名称'
  },
  {
    prop: 'feeCode',
    label: '费用编码',
    type: 'input',
    placeholder: '请输入费用编码（如：PROPERTY_FEE）',
    disabled: isEdit.value
  },
  {
    prop: 'unitPrice',
    label: '单价',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入单价',
    prepend: '¥'
  },
  {
    prop: 'unitType',
    label: '计费单位',
    type: 'select',
    options: unitTypeOptions,
    placeholder: '请选择计费单位'
  },
  {
    prop: 'billingCycle',
    label: '计费周期',
    type: 'select',
    options: billingCycleOptions,
    placeholder: '请选择计费周期'
  },
  {
    prop: 'description',
    label: '费用说明',
    type: 'textarea',
    placeholder: '请输入费用说明'
  },
  {
    prop: 'status',
    label: '状态',
    type: 'radio',
    options: [
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ]
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑费用类型' : '新增费用类型')

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

// 获取模拟数据
const getMockData = () => {
  const mockFeeTypes = [
    {
      feeTypeId: 1,
      feeName: '物业费',
      feeCode: 'PROPERTY_FEE',
      unitPrice: 2.5,
      unitType: '元/平方米',
      billingCycle: 1,
      description: '小区日常维护、保洁、安保等费用',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 2,
      feeName: '停车费',
      feeCode: 'PARKING_FEE',
      unitPrice: 200,
      unitType: '元/月',
      billingCycle: 1,
      description: '地下/地面停车位使用费',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 3,
      feeName: '垃圾处理费',
      feeCode: 'GARBAGE_FEE',
      unitPrice: 10,
      unitType: '元/户',
      billingCycle: 1,
      description: '生活垃圾收集和处理费用',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 4,
      feeName: '电梯费',
      feeCode: 'ELEVATOR_FEE',
      unitPrice: 30,
      unitType: '元/户',
      billingCycle: 1,
      description: '电梯运行、维护、年检等费用',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 5,
      feeName: '公共照明费',
      feeCode: 'PUBLIC_LIGHTING_FEE',
      unitPrice: 15,
      unitType: '元/户',
      billingCycle: 1,
      description: '楼道、公共区域照明电费',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 6,
      feeName: '水费',
      feeCode: 'WATER_FEE',
      unitPrice: 4.5,
      unitType: '元/吨',
      billingCycle: 1,
      description: '公共区域用水费用',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 7,
      feeName: '电费',
      feeCode: 'ELECTRICITY_FEE',
      unitPrice: 0.8,
      unitType: '元/度',
      billingCycle: 1,
      description: '公共区域用电费用',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    },
    {
      feeTypeId: 8,
      feeName: '维修基金',
      feeCode: 'MAINTENANCE_FUND',
      unitPrice: 1000,
      unitType: '元/年',
      billingCycle: 3,
      description: '房屋公共部位、设施设备维修基金',
      status: 1,
      createTime: '2024-01-01 10:00:00'
    }
  ]

  // 模拟分页
  pagination.total = mockFeeTypes.length
  return mockFeeTypes
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  handleSearch()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟API请求
    setTimeout(() => {
      tableData.value = getMockData()
      loading.value = false
    }, 500)
  } catch (error) {
    loading.value = false
    ElMessage.error('获取数据失败')
  }
}

// 分页变化
const handlePageChange = (page) => {
  pagination.current = page
  fetchData()
}

// 排序变化
const handleSortChange = (sort) => {
  console.log('排序变化:', sort)
  fetchData()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除费用类型"${row.typeName}"吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的${selectedRows.value.length}个费用类型吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看账单
const handleViewBills = (row) => {
  ElMessage.info(`查看费用类型"${row.typeName}"的账单记录`)
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
    submitLoading.value = true

    // 模拟API请求
    setTimeout(() => {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
      submitLoading.value = false
    }, 1000)
  } catch (error) {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    typeId: null,
    typeName: '',
    typeCode: '',
    unitPrice: 0,
    billingUnit: '',
    billingCycle: 1,
    description: '',
    status: 1
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 组件挂载
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }

  .price-text {
    color: #f56c6c;
    font-weight: bold;
  }
}
</style>