<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">楼栋管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>楼栋管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="楼栋编号">
          <el-input
            v-model="searchForm.buildingNo"
            placeholder="请输入楼栋编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="楼栋名称">
          <el-input
            v-model="searchForm.buildingName"
            placeholder="请输入楼栋名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="建筑年份">
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
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增楼栋
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

    <!-- 楼栋表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="buildingNo" label="楼栋编号" width="120" sortable />
        <el-table-column prop="buildingName" label="楼栋名称" width="150" show-overflow-tooltip />
        <el-table-column prop="floorCount" label="楼层数" width="100" />
        <el-table-column prop="unitCount" label="单元数" width="100" />
        <el-table-column prop="address" label="详细地址" show-overflow-tooltip />
        <el-table-column prop="buildYear" label="建筑年份" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
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
              @click="handleViewUnits(row)"
            >
              查看单元
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

    <!-- 新增/编辑楼栋对话框 -->
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
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input v-model="form.buildingNo" placeholder="请输入楼栋编号" />
        </el-form-item>
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单元数" prop="unitCount">
          <el-input-number v-model="form.unitCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="建筑年份" prop="buildYear">
          <el-date-picker
            v-model="form.buildYear"
            type="year"
            placeholder="选择建筑年份"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看单元对话框 -->
    <el-dialog
      v-model="unitDialogVisible"
      title="单元列表"
      width="800px"
    >
      <el-table :data="unitData" border>
        <el-table-column prop="unitNo" label="单元编号" width="120" />
        <el-table-column prop="unitName" label="单元名称" width="120" />
        <el-table-column prop="floorCount" label="楼层数" width="100" />
        <el-table-column prop="roomCountPerFloor" label="每层房间数" width="120" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewHouses(row)">
              查看房产
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="unitDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'

// 响应式数据
const formRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const unitDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const unitData = ref([])
const submitLoading = ref(false)

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


// 表单数据
const form = reactive({
  id: null,
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
    { required: true, message: '请输入楼层数', trigger: 'blur' }
  ],
  unitCount: [
    { required: true, message: '请输入单元数', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑楼栋' : '新增楼栋')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生成模拟数据
const generateMockData = () => {
  const buildings = [
    {
      id: 1,
      buildingNo: 'A01',
      buildingName: '1号楼',
      floorCount: 18,
      unitCount: 3,
      address: '北京市朝阳区xx街道xx小区1号楼',
      buildYear: 2020,
      remark: '高层住宅楼',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 2,
      buildingNo: 'A02',
      buildingName: '2号楼',
      floorCount: 24,
      unitCount: 4,
      address: '北京市朝阳区xx街道xx小区2号楼',
      buildYear: 2021,
      remark: '超高层住宅楼',
      createTime: new Date('2024-01-02 10:00:00').toISOString()
    },
    {
      id: 3,
      buildingNo: 'B01',
      buildingName: '3号楼',
      floorCount: 12,
      unitCount: 2,
      address: '北京市朝阳区xx街道xx小区3号楼',
      buildYear: 2019,
      remark: '小高层住宅楼',
      createTime: new Date('2024-01-03 10:00:00').toISOString()
    },
    {
      id: 4,
      buildingNo: 'B02',
      buildingName: '4号楼',
      floorCount: 6,
      unitCount: 1,
      address: '北京市朝阳区xx街道xx小区4号楼',
      buildYear: 2018,
      remark: '多层住宅楼',
      createTime: new Date('2024-01-04 10:00:00').toISOString()
    }
  ]

  return buildings
}

// 生成模拟单元数据
const generateMockUnitData = (buildingId) => {
  const units = [
    {
      id: 1,
      buildingId: 1,
      unitNo: 'A01-1',
      unitName: '1单元',
      floorCount: 18,
      roomCountPerFloor: 3,
      remark: '一梯三户',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 2,
      buildingId: 1,
      unitNo: 'A01-2',
      unitName: '2单元',
      floorCount: 18,
      roomCountPerFloor: 3,
      remark: '一梯三户',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 3,
      buildingId: 1,
      unitNo: 'A01-3',
      unitName: '3单元',
      floorCount: 18,
      roomCountPerFloor: 3,
      remark: '一梯三户',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    }
  ]

  return units.filter(unit => unit.buildingId === buildingId)
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadBuildings()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    buildingNo: '',
    buildingName: '',
    buildYearRange: []
  })
  handleSearch()
}

// 加载楼栋数据
const loadBuildings = () => {
  loading.value = true
  setTimeout(() => {
    const mockData = generateMockData()
    tableData.value = mockData.slice(
      (pagination.current - 1) * pagination.pageSize,
      pagination.current * pagination.pageSize
    )
    pagination.total = mockData.length
    loading.value = false
  }, 500)
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadBuildings()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadBuildings()
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
    loadBuildings()
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
    loadBuildings()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看单元
const handleViewUnits = (row) => {
  unitData.value = generateMockUnitData(row.id)
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
      loadBuildings()
      submitLoading.value = false
    }, 1000)
  } catch (error) {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
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

// 初始化
onMounted(() => {
  loadBuildings()
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
</style>