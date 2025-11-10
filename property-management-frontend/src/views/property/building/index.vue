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
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input
            v-model="searchForm.buildingNo"
            placeholder="请输入楼栋编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input
            v-model="searchForm.buildingName"
            placeholder="请输入楼栋名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="建筑年份" prop="buildYear">
          <el-date-picker
            v-model="searchForm.buildYearRange"
            type="yearrange"
            range-separator="至"
            start-placeholder="开始年份"
            end-placeholder="结束年份"
            value-format="YYYY"
            style="width: 240px"
          />
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
          <span>楼栋列表</span>
          <div class="header-actions">
            <el-button
              type="primary"
              v-permission="'property:building:add'"
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增楼栋
            </el-button>
            <el-button
              type="danger"
              v-permission="'property:building:delete'"
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
            v-permission="'property:building:edit'"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="info"
            @click="handleViewUnits(row)"
          >
            查看单元
          </el-button>
          <el-button
            link
            type="danger"
            v-permission="'property:building:delete'"
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

    <!-- 查看单元对话框 -->
    <el-dialog
      v-model="unitDialogVisible"
      title="单元列表"
      width="800px"
    >
      <el-table :data="unitData" border>
        <el-table-column prop="unitCode" label="单元编号" width="120" />
        <el-table-column prop="unitName" label="单元名称" width="120" />
        <el-table-column prop="floorCount" label="楼层数" width="100" />
        <el-table-column prop="roomsPerFloor" label="每层房间数" width="120" />
        <el-table-column prop="totalRooms" label="总房间数" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewHouses(row)">
              查看房产
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="unitDialogVisible = false">关闭</el-button>
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
const unitDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const unitData = ref([])

// 搜索表单
const searchForm = reactive({
  buildingNo: '',
  buildingName: '',
  buildYearRange: []
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
    prop: 'buildingNo',
    label: '楼栋编号',
    width: '120',
    sortable: true
  },
  {
    prop: 'buildingName',
    label: '楼栋名称',
    width: '150',
    sortable: true
  },
  {
    prop: 'floorCount',
    label: '楼层数',
    width: '100'
  },
  {
    prop: 'unitCount',
    label: '单元数',
    width: '100'
  },
  {
    prop: 'totalHouseholds',
    label: '总户数',
    width: '100'
  },
  {
    prop: 'address',
    label: '详细地址',
    showOverflowTooltip: true
  },
  {
    prop: 'buildYear',
    label: '建筑年份',
    width: '100',
    sortable: true
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

// 表单数据
const form = reactive({
  buildingId: null,
  buildingNo: '',
  buildingName: '',
  floorCount: 1,
  unitCount: 1,
  address: '',
  buildYear: new Date().getFullYear(),
  remark: ''
})

// 表单规则
const formRules = {
  buildingNo: [
    { required: true, message: '请输入楼栋编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '楼栋编号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  buildingName: [
    { required: true, message: '请输入楼栋名称', trigger: 'blur' },
    { min: 2, max: 50, message: '楼栋名称长度在2到50个字符', trigger: 'blur' }
  ],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 99, message: '楼层数必须在1-99之间', trigger: 'blur' }
  ],
  unitCount: [
    { required: true, message: '请输入单元数', trigger: 'blur' },
    { type: 'number', min: 1, max: 20, message: '单元数必须在1-20之间', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  buildYear: [
    { required: true, message: '请选择建筑年份', trigger: 'change' },
    { type: 'number', min: 1900, max: new Date().getFullYear(), message: '请输入有效的建筑年份', trigger: 'blur' }
  ]
}

// 表单项配置
const formItems = computed(() => [
  {
    prop: 'buildingNo',
    label: '楼栋编号',
    type: 'input',
    placeholder: '请输入楼栋编号（如：A01）',
    disabled: isEdit.value
  },
  {
    prop: 'buildingName',
    label: '楼栋名称',
    type: 'input',
    placeholder: '请输入楼栋名称（如：1号楼）'
  },
  {
    prop: 'floorCount',
    label: '楼层数',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入楼层数'
  },
  {
    prop: 'unitCount',
    label: '单元数',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入单元数'
  },
  {
    prop: 'address',
    label: '详细地址',
    type: 'input',
    placeholder: '请输入详细地址'
  },
  {
    prop: 'buildYear',
    label: '建筑年份',
    type: 'input',
    inputType: 'number',
    placeholder: '请输入建筑年份'
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息'
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑楼栋' : '新增楼栋')

// 获取模拟数据
const getMockData = () => {
  const mockBuildings = [
    {
      buildingId: 1,
      buildingNo: 'A01',
      buildingName: '1号楼',
      floorCount: 18,
      unitCount: 3,
      totalHouseholds: 162,
      address: '北京市朝阳区xx街道xx小区1号楼',
      buildYear: 2020,
      createTime: '2024-01-01 10:00:00'
    },
    {
      buildingId: 2,
      buildingNo: 'A02',
      buildingName: '2号楼',
      floorCount: 24,
      unitCount: 4,
      totalHouseholds: 288,
      address: '北京市朝阳区xx街道xx小区2号楼',
      buildYear: 2021,
      createTime: '2024-01-02 10:00:00'
    },
    {
      buildingId: 3,
      buildingNo: 'B01',
      buildingName: '3号楼',
      floorCount: 12,
      unitCount: 2,
      totalHouseholds: 96,
      address: '北京市朝阳区xx街道xx小区3号楼',
      buildYear: 2019,
      createTime: '2024-01-03 10:00:00'
    },
    {
      buildingId: 4,
      buildingNo: 'B02',
      buildingName: '4号楼',
      floorCount: 6,
      unitCount: 1,
      totalHouseholds: 24,
      address: '北京市朝阳区xx街道xx小区4号楼',
      buildYear: 2018,
      createTime: '2024-01-04 10:00:00'
    }
  ]

  // 模拟分页
  pagination.total = mockBuildings.length
  return mockBuildings
}

// 获取模拟单元数据
const getMockUnitData = (buildingId) => {
  const unitCount = [
    {
      unitId: 1,
      buildingId: 1,
      unitCode: 'A01-1',
      unitName: '1单元',
      floorCount: 18,
      roomsPerFloor: 3,
      totalRooms: 54,
      remark: '一梯三户',
      createTime: '2024-01-01 10:00:00'
    },
    {
      unitId: 2,
      buildingId: 1,
      unitCode: 'A01-2',
      unitName: '2单元',
      floorCount: 18,
      roomsPerFloor: 3,
      totalRooms: 54,
      remark: '一梯三户',
      createTime: '2024-01-01 10:00:00'
    },
    {
      unitId: 3,
      buildingId: 1,
      unitCode: 'A01-3',
      unitName: '3单元',
      floorCount: 18,
      roomsPerFloor: 3,
      totalRooms: 54,
      remark: '一梯三户',
      createTime: '2024-01-01 10:00:00'
    }
  ]

  return unitCount.filter(unit => unit.buildingId === buildingId)
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
      `确定要删除楼栋"${row.buildingName}"吗？`,
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
      `确定要删除选中的${selectedRows.value.length}个楼栋吗？`,
      '提示',
      { type: 'warning' }
    )
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看单元
const handleViewUnits = (row) => {
  unitData.value = getMockUnitData(row.buildingId)
  unitDialogVisible.value = true
}

// 查看房产
const handleViewHouses = (row) => {
  ElMessage.info(`查看单元${row.unitName}的房产信息`)
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

    // 计算总户数
    const totalHouseholds = form.floorCount * form.unitCount * 3 // 假设每层3户

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
    buildingId: null,
    buildingNo: '',
    buildingName: '',
    floorCount: 1,
    unitCount: 1,
    address: '',
    buildYear: new Date().getFullYear(),
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