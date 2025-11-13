<template>
  <div class="log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">房产管理</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物业管理</el-breadcrumb-item>
        <el-breadcrumb-item>房产管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="房产编号">
          <el-input
            v-model="searchForm.houseNo"
            placeholder="请输入房产编号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="门牌号">
          <el-input
            v-model="searchForm.roomNumber"
            placeholder="请输入门牌号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="所属楼栋">
          <el-select
            v-model="searchForm.buildingId"
            placeholder="请选择楼栋"
            clearable
            style="width: 150px"
            @change="handleBuildingChange"
          >
            <el-option
              v-for="item in buildingOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属单元">
          <el-select
            v-model="searchForm.unitId"
            placeholder="请选择单元"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in unitOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房产状态">
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
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        @click="handleAdd"
      >
        <el-icon><Plus /></el-icon>
        新增房产
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

    <!-- 房产表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="houseNo" label="房产编号" width="140" sortable />
        <el-table-column prop="buildingName" label="楼栋名称" width="100" />
        <el-table-column prop="unitName" label="单元名称" width="100" />
        <el-table-column prop="roomNumber" label="门牌号" width="100" sortable />
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
              @click="handleViewResident(row)"
            >
              查看住户
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

    <!-- 新增/编辑房产对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择所属楼栋" style="width: 100%" @change="handleFormBuildingChange">
            <el-option
              v-for="item in buildingOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属单元" prop="unitId">
          <el-select v-model="form.unitId" placeholder="请选择所属单元" style="width: 100%">
            <el-option
              v-for="item in unitOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房产编号" prop="houseNo">
          <el-input v-model="form.houseNo" placeholder="请输入房产编号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="门牌号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入门牌号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="户型" prop="houseType">
          <el-select v-model="form.houseType" placeholder="请选择户型" style="width: 100%">
            <el-option
              v-for="item in houseTypeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="建筑面积" prop="buildingArea">
          <el-input-number v-model="form.buildingArea" :min="1" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="使用面积" prop="usableArea">
          <el-input-number v-model="form.usableArea" :min="1" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="房产状态" prop="houseStatus">
          <el-select v-model="form.houseStatus" placeholder="请选择房产状态" style="width: 100%">
            <el-option
              v-for="item in houseStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="产权人" prop="propertyOwner">
          <el-input v-model="form.propertyOwner" placeholder="请输入产权人姓名" />
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

    <!-- 查看住户对话框 -->
    <el-dialog
      v-model="residentDialogVisible"
      title="住户信息"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="产权人">
          {{ currentResident.propertyOwner || '暂无' }}
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
        <el-button @click="residentDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'

// 响应式数据
const formRef = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const residentDialogVisible = ref(false)
const selectedRows = ref([])
const isEdit = ref(false)
const currentResident = ref({})

// 搜索表单
const searchForm = reactive({
  houseNo: '',
  roomNumber: '',
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

// 选项数据
const buildingOptions = ref([
  { label: '1号楼', value: 1 },
  { label: '2号楼', value: 2 },
  { label: '3号楼', value: 3 },
  { label: '4号楼', value: 4 }
])

const unitOptions = ref([])

const houseStatusOptions = [
  { label: '空置', value: 1 },
  { label: '已售', value: 2 },
  { label: '已租', value: 3 },
  { label: '自住', value: 4 }
]

const houseTypeOptions = [
  '一室一厅',
  '两室一厅',
  '三室两厅',
  '四室两厅'
]

// 表单数据
const form = reactive({
  id: null,
  buildingId: '',
  unitId: '',
  houseNo: '',
  roomNumber: '',
  floor: 1,
  houseType: '',
  buildingArea: 0,
  usableArea: 0,
  houseStatus: 1,
  propertyOwner: '',
  remark: ''
})

// 表单规则
const formRules = {
  buildingId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  unitId: [
    { required: true, message: '请选择所属单元', trigger: 'change' }
  ],
  houseNo: [
    { required: true, message: '请输入房产编号', trigger: 'blur' }
  ],
  roomNumber: [
    { required: true, message: '请输入门牌号', trigger: 'blur' }
  ],
  floor: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  houseType: [
    { required: true, message: '请选择户型', trigger: 'change' }
  ],
  buildingArea: [
    { required: true, message: '请输入建筑面积', trigger: 'blur' }
  ],
  usableArea: [
    { required: true, message: '请输入使用面积', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑房产' : '新增房产')

// 监听建筑面积变化，自动计算使用面积
watch(() => form.buildingArea, (newValue) => {
  if (newValue && !form.usableArea) {
    form.usableArea = Math.floor(newValue * 0.8) // 默认使用面积为建筑面积的80%
  }
})

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
  unitOptions.value = getMockUnitOptions(buildingId)
}

// 表单中楼栋变化处理
const handleFormBuildingChange = (buildingId) => {
  form.unitId = ''
  unitOptions.value = getMockUnitOptions(buildingId)
}

// 获取模拟单元选项
const getMockUnitOptions = (buildingId) => {
  const units = [
    { label: '1单元', value: 1, buildingId: 1 },
    { label: '2单元', value: 2, buildingId: 1 },
    { label: '3单元', value: 3, buildingId: 1 },
    { label: '1单元', value: 4, buildingId: 2 },
    { label: '2单元', value: 5, buildingId: 2 },
    { label: '3单元', value: 6, buildingId: 2 },
    { label: '4单元', value: 7, buildingId: 2 },
    { label: '1单元', value: 8, buildingId: 3 },
    { label: '2单元', value: 9, buildingId: 3 }
  ]
  return units.filter(unit => unit.buildingId === buildingId)
}

// 生成模拟数据
const generateMockData = () => {
  const houses = []
  const owners = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']

  // 为每个单元生成房产数据
  for (let i = 1; i <= 9; i++) {
    const buildingId = i <= 3 ? 1 : (i <= 7 ? 2 : 3)
    const buildingName = buildingId === 1 ? '1号楼' : (buildingId === 2 ? '2号楼' : '3号楼')
    const unitName = `${i <= 3 ? i : (i <= 7 ? i-3 : i-7)}单元`

    for (let floor = 1; floor <= 6; floor++) {
      for (let room = 1; room <= 3; room++) {
        const houseStatus = Math.floor(Math.random() * 4) + 1
        const houseType = houseTypeOptions[Math.floor(Math.random() * houseTypeOptions.length)]
        const buildingArea = 80 + Math.floor(Math.random() * 120)
        const usableArea = Math.floor(buildingArea * 0.8)

        houses.push({
          id: `house_${i}_${floor}_${room}`,
          buildingId: buildingId,
          buildingName: buildingName,
          unitId: i,
          unitName: unitName,
          houseNo: `H${buildingId.toString().padStart(2, '0')}${i.toString().padStart(2, '0')}${floor.toString().padStart(2, '0')}${room.toString().padStart(2, '0')}`,
          roomNumber: `${floor}0${room}`,
          floor: floor,
          houseType: houseType,
          buildingArea: buildingArea,
          usableArea: usableArea,
          houseStatus: houseStatus,
          propertyOwner: houseStatus > 1 ? owners[Math.floor(Math.random() * owners.length)] : '',
          remark: '',
          createTime: new Date('2024-01-01 10:00:00').toISOString()
        })
      }
    }
  }

  return houses
}

// 加载房产数据
const loadHouses = () => {
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
  loadHouses()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    houseNo: '',
    roomNumber: '',
    buildingId: '',
    unitId: '',
    houseStatus: ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    buildingId: '',
    unitId: '',
    houseNo: '',
    roomNumber: '',
    floor: 1,
    houseType: '',
    buildingArea: 0,
    usableArea: 0,
    houseStatus: 1,
    propertyOwner: '',
    remark: ''
  })
  unitOptions.value = []
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  // 设置单元选项
  unitOptions.value = getMockUnitOptions(row.buildingId)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除房产"${row.houseNo}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadHouses()
  }).catch(() => {
    // 用户取消操作
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的房产')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的${selectedRows.value.length}个房产吗？`,
    '批量删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('批量删除成功')
    loadHouses()
  })
}

// 查看住户
const handleViewResident = (row) => {
  // 模拟住户数据
  currentResident.value = {
    propertyOwner: row.propertyOwner || '暂无',
    ownerPhone: row.propertyOwner ? '138****' + Math.floor(Math.random() * 10000) : '暂无',
    ownerIdCard: row.propertyOwner ? '110****' + Math.floor(Math.random() * 1000000000000) : '暂无',
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
const handleSubmit = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogTitle.value + '成功')
      dialogVisible.value = false
      loadHouses()
    }
  })
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadHouses()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadHouses()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 初始化
onMounted(() => {
  loadHouses()
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