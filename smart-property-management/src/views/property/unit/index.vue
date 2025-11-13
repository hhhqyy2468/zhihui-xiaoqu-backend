<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">单元管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>单元管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="单元编号">
          <el-input
            v-model="searchForm.unitNo"
            placeholder="请输入单元编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="单元名称">
          <el-input
            v-model="searchForm.unitName"
            placeholder="请输入单元名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="所属楼栋">
          <el-select
            v-model="searchForm.buildingId"
            placeholder="请选择所属楼栋"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in buildingOptions"
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
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增单元
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

    <!-- 单元表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="unitNo" label="单元编号" width="120" sortable />
        <el-table-column prop="unitName" label="单元名称" width="120" />
        <el-table-column prop="buildingName" label="所属楼栋" width="120" />
        <el-table-column prop="floorCount" label="楼层数" width="100" />
        <el-table-column prop="roomCountPerFloor" label="每层房间数" width="120" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
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
              @click="handleViewHouses(row)"
            >
              查看房产
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

    <!-- 新增/编辑单元对话框 -->
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
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择所属楼栋" style="width: 100%">
            <el-option
              v-for="item in buildingOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="单元编号" prop="unitNo">
          <el-input v-model="form.unitNo" placeholder="请输入单元编号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="单元名称" prop="unitName">
          <el-input v-model="form.unitName" placeholder="请输入单元名称" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每层房间数" prop="roomCountPerFloor">
          <el-input-number v-model="form.roomCountPerFloor" :min="1" style="width: 100%" />
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

    <!-- 查看房产对话框 -->
    <el-dialog
      v-model="houseDialogVisible"
      title="房产列表"
      width="900px"
    >
      <el-table :data="houseData" border max-height="400">
        <el-table-column prop="houseNo" label="房产编号" width="120" />
        <el-table-column prop="roomNumber" label="门牌号" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="houseType" label="户型" width="100" />
        <el-table-column prop="buildingArea" label="建筑面积" width="120">
          <template #default="{ row }">
            {{ row.buildingArea }}m²
          </template>
        </el-table-column>
        <el-table-column prop="usableArea" label="使用面积" width="120">
          <template #default="{ row }">
            {{ row.usableArea }}m²
          </template>
        </el-table-column>
        <el-table-column prop="houseStatus" label="房产状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHouseStatusTag(row.houseStatus)">
              {{ getHouseStatusName(row.houseStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="propertyOwner" label="产权人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewHouseDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="houseDialogVisible = false">关闭</el-button>
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
const houseDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const houseData = ref([])

// 搜索表单
const searchForm = reactive({
  unitNo: '',
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

// 楼栋选项
const buildingOptions = ref([
  { label: '1号楼', value: 1 },
  { label: '2号楼', value: 2 },
  { label: '3号楼', value: 3 },
  { label: '4号楼', value: 4 }
])

// 表单数据
const form = reactive({
  id: null,
  buildingId: '',
  unitNo: '',
  unitName: '',
  floorCount: 1,
  roomCountPerFloor: 3,
  remark: ''
})

// 表单规则
const formRules = {
  buildingId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  unitNo: [
    { required: true, message: '请输入单元编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '单元编号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  unitName: [
    { required: true, message: '请输入单元名称', trigger: 'blur' },
    { min: 2, max: 50, message: '单元名称长度在2到50个字符', trigger: 'blur' }
  ],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' }
  ],
  roomCountPerFloor: [
    { required: true, message: '请输入每层房间数', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑单元' : '新增单元')

// 房产状态选项
const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

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

// 生成模拟数据
const generateMockData = () => {
  const units = [
    {
      id: 1,
      buildingId: 1,
      unitNo: 'A01-1',
      unitName: '1单元',
      buildingName: '1号楼',
      floorCount: 18,
      roomCountPerFloor: 3,
      remark: '一梯三户，南北通透',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 2,
      buildingId: 1,
      unitNo: 'A01-2',
      unitName: '2单元',
      buildingName: '1号楼',
      floorCount: 18,
      roomCountPerFloor: 3,
      remark: '一梯三户，南北通透',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 3,
      buildingId: 1,
      unitNo: 'A01-3',
      unitName: '3单元',
      buildingName: '1号楼',
      floorCount: 18,
      roomCountPerFloor: 3,
      remark: '一梯三户，南北通透',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    },
    {
      id: 4,
      buildingId: 2,
      unitNo: 'A02-1',
      unitName: '1单元',
      buildingName: '2号楼',
      floorCount: 24,
      roomCountPerFloor: 4,
      remark: '一梯四户，高层住宅',
      createTime: new Date('2024-01-02 10:00:00').toISOString()
    },
    {
      id: 5,
      buildingId: 2,
      unitNo: 'A02-2',
      unitName: '2单元',
      buildingName: '2号楼',
      floorCount: 24,
      roomCountPerFloor: 4,
      remark: '一梯四户，高层住宅',
      createTime: new Date('2024-01-02 10:00:00').toISOString()
    },
    {
      id: 6,
      buildingId: 3,
      unitNo: 'B01-1',
      unitName: '1单元',
      buildingName: '3号楼',
      floorCount: 12,
      roomCountPerFloor: 2,
      remark: '一梯两户，小高层',
      createTime: new Date('2024-01-03 10:00:00').toISOString()
    }
  ]

  return units
}

// 生成模拟房产数据
const generateMockHouseData = (unitId) => {
  const houses = []
  const houseLayouts = ['一室一厅', '两室一厅', '三室两厅', '四室两厅']
  const houseStatuses = [1, 2, 3, 4]
  const ownerNames = ['张三', '李四', '王五', '赵六', '钱七', '孙八']

  // 为指定单元生成部分房产数据
  for (let i = 1; i <= 10; i++) {
    const houseStatus = houseStatuses[Math.floor(Math.random() * houseStatuses.length)]
    const layout = houseLayouts[Math.floor(Math.random() * houseLayouts.length)]
    const buildingArea = 80 + Math.floor(Math.random() * 100)
    const usableArea = Math.floor(buildingArea * 0.8)

    houses.push({
      id: `${unitId}-${i}`,
      unitId: unitId,
      houseNo: `${unitId}-${String(i).padStart(3, '0')}`,
      roomNumber: `${Math.floor(Math.random() * 18) + 1}0${i}`,
      floor: Math.floor(Math.random() * 18) + 1,
      houseType: layout,
      buildingArea: buildingArea,
      usableArea: usableArea,
      houseStatus: houseStatus,
      propertyOwner: houseStatus > 1 ? ownerNames[Math.floor(Math.random() * ownerNames.length)] : '',
      createTime: new Date('2024-01-01 10:00:00').toISOString()
    })
  }

  return houses
}

// 加载单元数据
const loadUnits = () => {
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

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadUnits()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    unitNo: '',
    unitName: '',
    buildingId: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    buildingId: '',
    unitNo: '',
    unitName: '',
    floorCount: 1,
    roomCountPerFloor: 3,
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除单元"${row.unitName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadUnits()
  }).catch(() => {
    // 用户取消操作
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的单元')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的${selectedRows.value.length}个单元吗？`,
    '批量删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量删除成功')
    loadUnits()
  })
}

// 查看房产
const handleViewHouses = (row) => {
  houseData.value = generateMockHouseData(row.id)
  houseDialogVisible.value = true
}

// 查看房产详情
const handleViewHouseDetail = (row) => {
  ElMessage.info(`查看房产${row.houseNo}的详细信息`)
}

// 导出
const handleExport = () => {
  ElMessage.success('导出成功')
}

// 提交表单
const handleSubmit = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadUnits()
    }
  })
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadUnits()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadUnits()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 初始化
onMounted(() => {
  loadUnits()
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