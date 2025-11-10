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
        <el-form-item label="单元编号" prop="unitCode">
          <el-input
            v-model="searchForm.unitCode"
            placeholder="请输入单元编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="单元名称" prop="unitName">
          <el-input
            v-model="searchForm.unitName"
            placeholder="请输入单元名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select
            v-model="searchForm.buildingId"
            placeholder="请选择所属楼栋"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in buildingOptions"
              :key="item.buildingId"
              :label="item.buildingName"
              :value="item.buildingId"
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
          <span>单元列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:unit:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增单元
            </el-button>
            <el-button
              type="danger"
              v-permission="'property:unit:delete'"
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
        <!-- 操作列 -->
        <template #operation="{ row }">
          <el-button
            link
            type="primary"
            v-permission="'property:unit:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="info"
            @click="handleViewHouses(row)"
          >
            查看房产
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'property:unit:delete'"
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

    <!-- 查看房产对话框 -->
    <el-dialog
      v-model="houseDialogVisible"
      title="房产列表"
      width="900px"
    >
      <el-table :data="houseData" border max-height="400">
        <el-table-column prop="houseCode" label="房产编号" width="120" />
        <el-table-column prop="roomNum" label="门牌号" width="100" />
        <el-table-column prop="floorNum" label="楼层" width="80" />
        <el-table-column prop="houseType" label="户型" width="100" />
        <el-table-column prop="buildArea" label="建筑面积" width="120">
          <template #default="{ row }">
            {{ row.buildArea }}m²
          </template>
        </el-table-column>
        <el-table-column prop="useArea" label="使用面积" width="120">
          <template #default="{ row }">
            {{ row.useArea }}m²
          </template>
        </el-table-column>
        <el-table-column prop="houseStatus" label="房产状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHouseStatusTag(row.houseStatus)">
              {{ getHouseStatusName(row.houseStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="产权人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewHouseDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="houseDialogVisible = false">关闭</el-button>
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
const houseDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const houseData = ref([])

// 搜索表单
const searchForm = reactive({
  unitCode: '',
  unitName: '',
  buildingId: ''
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
    prop: 'unitCode',
    label: '单元编号',
    width: '120',
    sortable: true
  },
  {
    prop: 'unitName',
    label: '单元名称',
    width: '120',
    sortable: true
  },
  {
    prop: 'buildingName',
    label: '所属楼栋',
    width: '120'
  },
  {
    prop: 'floors',
    label: '楼层数',
    width: '100'
  },
  {
    prop: 'roomsPerFloor',
    label: '每层房间数',
    width: '120'
  },
  {
    prop: 'totalRooms',
    label: '总房间数',
    width: '100'
  },
  {
    prop: 'occupiedRooms',
    label: '已入住房间',
    width: '120'
  },
  {
    prop: 'occupancyRate',
    label: '入住率',
    width: '100',
    formatter: (row) => `${row.occupancyRate}%`
  },
  {
    prop: 'remark',
    label: '备注',
    showOverflowTooltip: true
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

// 楼栋选项
const buildingOptions = ref([
  { buildingId: 1, buildingName: '1号楼' },
  { buildingId: 2, buildingName: '2号楼' },
  { buildingId: 3, buildingName: '3号楼' },
  { buildingId: 4, buildingName: '4号楼' }
])

// 表单数据
const form = reactive({
  unitId: null,
  buildingId: '',
  unitCode: '',
  unitName: '',
  floors: 1,
  roomsPerFloor: 3,
  remark: ''
})

// 表单规则
const formRules = {
  buildingId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  unitCode: [
    { required: true, message: '请输入单元编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '单元编号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  unitName: [
    { required: true, message: '请输入单元名称', trigger: 'blur' },
    { min: 2, max: 20, message: '单元名称长度在2到20个字符', trigger: 'blur' }
  ],
  floors: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '楼层数必须在1-99之间', trigger: 'blur' }
  ],
  roomsPerFloor: [
    { required: true, message: '请输入每层房间数', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '每层房间数必须在1-10之间', trigger: 'blur' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'buildingId',
    label: '所属楼栋',
    type: 'select',
    options: buildingOptions.value,
    placeholder: '请选择所属楼栋'
  },
  {
    prop: 'unitCode',
    label: '单元编号',
    type: 'input',
    placeholder: '请输入单元编号（如：A01-1）',
    disabled: isEdit.value
  },
  {
    prop: 'unitName',
    label: '单元名称',
    type: 'input',
    placeholder: '请输入单元名称（如：1单元）'
  },
  {
    prop: 'floors',
    label: '楼层数',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入楼层数'
  },
  {
    prop: 'roomsPerFloor',
    label: '每层房间数',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入每层房间数'
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息'
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑单元' : '新增单元')

// 房产状态选项
const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

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

// 获取模拟数据
const getMockData = () => {
  const mockUnits = [
    {
      unitId: 1,
      buildingId: 1,
      unitCode: 'A01-1',
      unitName: '1单元',
      buildingName: '1号楼',
      floors: 18,
      roomsPerFloor: 3,
      totalRooms: 54,
      occupiedRooms: 42,
      occupancyRate: 78,
      remark: '一梯三户，南北通透',
      createTime: '2024-01-01 10:00:00'
    },
    {
      unitId: 2,
      buildingId: 1,
      unitCode: 'A01-2',
      unitName: '2单元',
      buildingName: '1号楼',
      floors: 18,
      roomsPerFloor: 3,
      totalRooms: 54,
      occupiedRooms: 48,
      occupancyRate: 89,
      remark: '一梯三户，南北通透',
      createTime: '2024-01-01 10:00:00'
    },
    {
      unitId: 3,
      buildingId: 1,
      unitCode: 'A01-3',
      unitName: '3单元',
      buildingName: '1号楼',
      floors: 18,
      roomsPerFloor: 3,
      totalRooms: 54,
      occupiedRooms: 36,
      occupancyRate: 67,
      remark: '一梯三户，南北通透',
      createTime: '2024-01-01 10:00:00'
    },
    {
      unitId: 4,
      buildingId: 2,
      unitCode: 'A02-1',
      unitName: '1单元',
      buildingName: '2号楼',
      floors: 24,
      roomsPerFloor: 4,
      totalRooms: 96,
      occupiedRooms: 72,
      occupancyRate: 75,
      remark: '一梯四户，高层住宅',
      createTime: '2024-01-02 10:00:00'
    }
  ]

  // 模拟分页
  pagination.total = mockUnits.length
  return mockUnits
}

// 获取模拟房产数据
const getMockHouseData = (unitId) => {
  const houses = []

  // 为指定单元生成房产数据
  const houseLayouts = ['一室一厅', '两室一厅', '三室两厅', '四室两厅']
  const houseStatuses = [1, 2, 3, 4]
  const ownerNames = ['张三', '李四', '王五', '赵六', '钱七', '孙八']

  for (let floor = 1; floor <= 18; floor++) {
    for (let room = 1; room <= 3; room++) {
      const houseStatus = houseStatuses[Math.floor(Math.random() * houseStatuses.length)]
      const layout = houseLayouts[Math.floor(Math.random() * houseLayouts.length)]
      const buildArea = 80 + Math.floor(Math.random() * 100)
      const useArea = Math.floor(buildArea * 0.8)

      houses.push({
        houseId: `${unitId}-${floor}-${room}`,
        unitId: unitId,
        houseCode: `${unitId}-${floor.toString().padStart(2, '0')}${room.toString().padStart(2, '0')}`,
        roomNum: `${floor}0${room}`,
        floorNum: floor,
        houseType: layout,
        buildArea: buildArea,
        useArea: useArea,
        houseStatus: houseStatus,
        ownerName: houseStatus > 1 ? ownerNames[Math.floor(Math.random() * ownerNames.length)] : '',
        createTime: '2024-01-01 10:00:00'
      })
    }
  }

  return houses
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
      `确定要删除单元"${row.unitName}"吗？`,
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
      `确定要删除选中的${selectedRows.value.length}个单元吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看房产
const handleViewHouses = (row) => {
  houseData.value = getMockHouseData(row.unitId)
  houseDialogVisible.value = true
}

// 查看房产详情
const handleViewHouseDetail = (row) => {
  ElMessage.info(`查看房产${row.houseCode}的详细信息`)
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

    // 获取楼栋名称
    const building = buildingOptions.value.find(b => b.buildingId === form.buildingId)

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
    unitId: null,
    buildingId: '',
    unitCode: '',
    unitName: '',
    floors: 1,
    roomsPerFloor: 3,
    remark: ''
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