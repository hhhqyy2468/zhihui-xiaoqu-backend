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
        <el-form-item label="房产编号" prop="houseCode">
          <el-input
            v-model="searchForm.houseCode"
            placeholder="请输入房产编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="门牌号" prop="roomNum">
          <el-input
            v-model="searchForm.roomNum"
            placeholder="请输入门牌号"
            clearable
            style="width: 150px"
          />
        </el-form-item>

        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select
            v-model="searchForm.buildingId"
            placeholder="请选择楼栋"
            clearable
            style="width: 150px"
            @change="handleBuildingChange"
          >
            <el-option
              v-for="item in buildingOptions"
              :key="item.buildingId"
              :label="item.buildingName"
              :value="item.buildingId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="所属单元" prop="unitId">
          <el-select
            v-model="searchForm.unitId"
            placeholder="请选择单元"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in unitOptions"
              :key="item.unitId"
              :label="item.unitName"
              :value="item.unitId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="房产状态" prop="houseStatus">
          <el-select
            v-model="searchForm.houseStatus"
            placeholder="请选择房产状态"
            clearable
            style="width: 120px"
          >
            <el-option
              v-for="item in houseStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
          <span>房产列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:house:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增房产
            </el-button>
            <el-button
              type="danger"
              v-permission="'property:house:delete'"
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
        <!-- 房产状态列 -->
        <template #houseStatus="{ row }">
          <el-tag :type="getHouseStatusTag(row.houseStatus)">
            {{ getHouseStatusName(row.houseStatus) }}
          </el-tag>
        </template>

        <!-- 建筑面积列 -->
        <template #buildArea="{ row }">
          {{ row.buildArea }}m²
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'property:house:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="info"
            @click="handleViewResident(row)"
          >
            查看住户
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'property:house:delete'"
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
      width="700px"
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

    <!-- 查看住户对话框 -->
    <el-dialog
      v-model="residentDialogVisible"
      title="住户信息"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="产权人">
          {{ currentResident.ownerName || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentResident.ownerPhone || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证号">
          {{ currentResident.ownerIdCard || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="入住时间">
          {{ currentResident.checkInTime || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="住户类型">
          <el-tag :type="getResidentTypeTag(currentResident.residentType)">
            {{ getResidentTypeName(currentResident.residentType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="住户状态">
          <el-tag :type="currentResident.residentStatus === 1 ? 'success' : 'danger'">
            {{ currentResident.residentStatus === 1 ? '在住' : '已搬离' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="residentDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
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
const residentDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const currentResident = ref({})

// 搜索表单
const searchForm = reactive({
  houseCode: '',
  roomNum: '',
  buildingId: '',
  unitId: '',
  houseStatus: ''
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
    prop: 'houseCode',
    label: '房产编号',
    width: '140',
    sortable: true
  },
  {
    prop: 'buildingName',
    label: '楼栋名称',
    width: '100'
  },
  {
    prop: 'unitName',
    label: '单元名称',
    width: '100'
  },
  {
    prop: 'roomNum',
    label: '门牌号',
    width: '100',
    sortable: true
  },
  {
    prop: 'floorNum',
    label: '楼层',
    width: '80'
  },
  {
    prop: 'houseType',
    label: '户型',
    width: '100'
  },
  {
    prop: 'buildArea',
    label: '建筑面积',
    width: '120',
    slot: 'buildArea',
    sortable: true
  },
  {
    prop: 'useArea',
    label: '使用面积',
    width: '120',
    formatter: (row) => `${row.useArea}m²`
  },
  {
    prop: 'houseStatus',
    label: '房产状态',
    width: '100',
    slot: 'houseStatus'
  },
  {
    prop: 'ownerName',
    label: '产权人',
    width: '120'
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
    width: '220',
    slot: 'operation',
    fixed: 'right'
  }
]

// 选项数据
const buildingOptions = ref([
  { buildingId: 1, buildingName: '1号楼' },
  { buildingId: 2, buildingName: '2号楼' },
  { buildingId: 3, buildingName: '3号楼' },
  { buildingId: 4, buildingName: '4号楼' }
])

const unitOptions = ref([])

const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

const houseTypeOptions = [
  { label: '一室一厅', value: '一室一厅' },
  { label: '两室一厅', value: '两室一厅' },
  { label: '三室两厅', value: '三室两厅' },
  { label: '四室两厅', value: '四室两厅' }
]

// 表单数据
const form = reactive({
  houseId: null,
  buildingId: '',
  unitId: '',
  houseCode: '',
  roomNum: '',
  floorNum: 1,
  houseType: '',
  buildArea: 0,
  useArea: 0,
  houseStatus: 1,
  ownerName: ''
})

// 表单规则
const formRules = {
  buildingId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  unitId: [
    { required: true, message: '请选择所属单元', trigger: 'change' }
  ],
  houseCode: [
    { required: true, message: '请输入房产编号', trigger: 'blur' }
  ],
  roomNum: [
    { required: true, message: '请输入门牌号', trigger: 'blur' }
  ],
  floorNum: [
    { required: true, message: '请输入楼层', trigger: 'blur' },
    { type: 'number', min: 1, message: '楼层必须大于0', trigger: 'blur' }
  ],
  houseType: [
    { required: true, message: '请选择户型', trigger: 'change' }
  ],
  buildArea: [
    { required: true, message: '请输入建筑面积', trigger: 'blur' },
    { type: 'number', min: 1, message: '建筑面积必须大于0', trigger: 'blur' }
  ],
  useArea: [
    { required: true, message: '请输入使用面积', trigger: 'blur' },
    { type: 'number', min: 1, message: '使用面积必须大于0', trigger: 'blur' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'buildingId',
    label: '所属楼栋',
    type: 'select',
    options: buildingOptions.value,
    placeholder: '请选择所属楼栋',
    onChange: handleBuildingChange
  },
  {
    prop: 'unitId',
    label: '所属单元',
    type: 'select',
    options: unitOptions.value,
    placeholder: '请选择所属单元'
  },
  {
    prop: 'houseCode',
    label: '房产编号',
    type: 'input',
    placeholder: '请输入房产编号',
    disabled: isEdit.value
  },
  {
    prop: 'roomNum',
    label: '门牌号',
    type: 'input',
    placeholder: '请输入门牌号'
  },
  {
    prop: 'floorNum',
    label: '楼层',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入楼层'
  },
  {
    prop: 'houseType',
    label: '户型',
    type: 'select',
    options: houseTypeOptions,
    placeholder: '请选择户型'
  },
  {
    prop: 'buildArea',
    label: '建筑面积',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入建筑面积（平方米）'
  },
  {
    prop: 'useArea',
    label: '使用面积',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入使用面积（平方米）'
  },
  {
    prop: 'houseStatus',
    label: '房产状态',
    type: 'select',
    options: houseStatusOptions,
    placeholder: '请选择房产状态'
  },
  {
    prop: 'ownerName',
    label: '产权人',
    type: 'input',
    placeholder: '请输入产权人姓名'
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑房产' : '新增房产')

// 监听建筑面积变化，自动计算使用面积
watch(() => form.buildArea, (newValue) => {
  if (newValue && !form.useArea) {
    form.useArea = Math.floor(newValue * 0.8) // 默认使用面积为建筑面积的80%
  }
})

// 获取房产状态名称
const getHouseStatusName = (status) => {
  const option = houseStatusOptions.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 获取房产状态标签
const getHouseStatusTag = (status) => {
  const tagMap = {
    1: 'info',    // 空置
    2: 'success', // 已售
    3: 'warning', // 已租
    4: 'primary'  // 自住
  }
  return tagMap[status] || 'info'
}

// 获取住户类型名称
const getResidentTypeName = (type) => {
  const typeMap = {
    1: '产权人',
    2: '租户',
    3: '家庭成员'
  }
  return typeMap[type] || '未知'
}

// 获取住户类型标签
const getResidentTypeTag = (type) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return tagMap[type] || 'info'
}

// 楼栋变化处理
const handleBuildingChange = (buildingId) => {
  searchForm.unitId = ''
  form.unitId = ''
  unitOptions.value = getMockUnitOptions(buildingId)
}

// 获取模拟单元选项
const getMockUnitOptions = (buildingId) => {
  const units = [
    { unitId: 1, unitName: '1单元', buildingId: 1 },
    { unitId: 2, unitName: '2单元', buildingId: 1 },
    { unitId: 3, unitName: '3单元', buildingId: 1 },
    { unitId: 4, unitName: '1单元', buildingId: 2 },
    { unitId: 5, unitName: '2单元', buildingId: 2 },
    { unitId: 6, unitName: '3单元', buildingId: 2 },
    { unitId: 7, unitName: '4单元', buildingId: 2 }
  ]
  return units.filter(unit => unit.buildingId === buildingId)
}

// 获取模拟数据
const getMockData = () => {
  const mockHouses = []
  const houseTypes = ['一室一厅', '两室一厅', '三室两厅', '四室两厅']
  const statuses = [1, 2, 3, 4]
  const owners = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']

  // 为每个单元生成房产数据
  for (let i = 1; i <= 7; i++) {
    for (let floor = 1; floor <= 18; floor++) {
      for (let room = 1; room <= 3; room++) {
        const houseStatus = statuses[Math.floor(Math.random() * statuses.length)]
        const houseType = houseTypes[Math.floor(Math.random() * houseTypes.length)]
        const buildArea = 80 + Math.floor(Math.random() * 120)
        const useArea = Math.floor(buildArea * 0.8)

        mockHouses.push({
          houseId: `house_${i}_${floor}_${room}`,
          buildingId: i <= 3 ? 1 : 2,
          buildingName: i <= 3 ? '1号楼' : '2号楼',
          unitId: i,
          unitName: `${i}单元`,
          houseCode: `H${i.toString().padStart(2, '0')}${floor.toString().padStart(2, '0')}${room.toString().padStart(2, '0')}`,
          roomNum: `${floor}0${room}`,
          floorNum: floor,
          houseType: houseType,
          buildArea: buildArea,
          useArea: useArea,
          houseStatus: houseStatus,
          ownerName: houseStatus > 1 ? owners[Math.floor(Math.random() * owners.length)] : '',
          createTime: '2024-01-01 10:00:00'
        })
      }
    }
  }

  // 模拟分页
  pagination.total = mockHouses.length
  return mockHouses
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
  // 设置单元选项
  unitOptions.value = getMockUnitOptions(row.buildingId)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除房产"${row.houseCode}"吗？`,
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
      `确定要删除选中的${selectedRows.value.length}个房产吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看住户
const handleViewResident = (row) => {
  // 模拟住户数据
  currentResident.value = {
    ownerName: row.ownerName || '暂无',
    ownerPhone: row.ownerName ? '138****' + Math.floor(Math.random() * 10000) : '暂无',
    ownerIdCard: row.ownerName ? '110****' + Math.floor(Math.random() * 1000000000000) : '暂无',
    checkInTime: row.houseStatus > 1 ? '2024-01-15' : '暂无',
    residentType: row.houseStatus === 4 ? 1 : 2, // 自住为产权人，其他为租户
    residentStatus: row.houseStatus > 1 ? 1 : 0
  }
  residentDialogVisible.value = true
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

    // 获取楼栋和单元名称
    const building = buildingOptions.value.find(b => b.buildingId === form.buildingId)
    const unit = unitOptions.value.find(u => u.unitId === form.unitId)

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
    houseId: null,
    buildingId: '',
    unitId: '',
    houseCode: '',
    roomNum: '',
    floorNum: 1,
    houseType: '',
    buildArea: 0,
    useArea: 0,
    houseStatus: 1,
    ownerName: ''
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
}
</style>